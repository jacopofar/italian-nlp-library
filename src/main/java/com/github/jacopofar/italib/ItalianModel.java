/*
* Copyright 2014 Jacopo Farina.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.jacopofar.italib;

import com.github.jacopofar.italib.ItalianVerbConjugation.ConjugationException;
import com.github.jacopofar.italib.postagger.POSUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * The main class to load an Italian language model and use it.
 * The model is based on Apache OpenNLP and the data extracted by com.github.jacopofar.conceptnetextractor
 * Currently, the OpenNLP models are from https://github.com/aciapetti/opennlp-italian-models
 * while the verb conjugations are from a dump of en.wiktionary
 * */
public class ItalianModel {
    private static final Logger logger = LogManager.getLogger(ItalianModel.class.getName());
    
    private final Cache<String,Span[]> tagCache=CacheBuilder.newBuilder().build();
            //new ConcurrentHashMap<>();
    private final static int MAX_POS_CACHE=300;
    private final HashSet<String> stopWords =new HashSet<>();
    public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException, ConjugationException {
        ItalianModel im = new ItalianModel();
        //showcase of functions
        ItalianVerbConjugation essere = new ItalianVerbConjugation(im);
        essere.setInfinitive("essere");
        essere.setMode("indicative present");
        essere.setNumber('s');
        essere.setPerson(3);
        
        System.out.println("lui "+essere.getConjugated());
        //String stmt="Lo ha detto il premier Matteo Renzi al termine del vertice Ue, a Bruxelles, un incontro che nonostante tutte le incognite lo lascia soddisfatto: 'Torniamo dall'Europa avendo vinto una battaglia di metodo e di sostanza', dice Renzi."
        //+ "Il mio numero di telefono personale è +39 0268680762836 e non +39 5868 6867 2439";
        String stmt="io vado in calabria al mare";
        System.err.println(stmt);
        String[] tokens =Span.spansToStrings(im.getTokens(stmt),stmt);
        int p=0;
        for(String t:im.quickPOSTag(stmt)){
            System.out.println(tokens[p]+" "+t+":"+POSUtils.getDescription(t));
            p++;
        }
        System.out.println("-----");
        for(String t:tokens)
            System.out.println(t+":"+Arrays.deepToString(im.getPoSvalues(t)));
        String[] verbi = {"andavamo","mangerò","volare","correre","puffavo","googlare"};
        HashMap<String,String> people=new HashMap<>(6);
        people.put("io", "1s");
        people.put("tu", "2s");
        people.put("lui", "3s");
        
        people.put("noi", "1p");
        people.put("voi", "2p");
        people.put("loro", "3p");
        
        for(String verbo :verbi){
            System.out.println("---");
            for(ItalianVerbConjugation v:im.getVerbs(verbo,true)){
                for(Entry<String, String> pers:people.entrySet()){
                    System.out.println(v);
                    v.setPerson(Integer.parseInt(pers.getValue().substring(0,1)));
                    v.setNumber(pers.getValue().charAt(1));
                    v.setMode("subjunctive imperfect");
                    try {
                        System.out.println("conjugation: che "+pers.getKey()+" "+v.getConjugated());
                    } catch (ConjugationException e) {
                        logger.error("error conjugating verb",e);
                    }
                }
            }
        }
        Set<String> forms = im.getAllKnownInfinitiveVerbs();
        System.out.println("There are "+forms.size()+" infinitive verbs in the database");
        System.out.println("Starting to count irregular forms in 10 seconds...");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            //doesn't really matter...
            //logger.error(e1);
        }
        ItalianVerbConjugation fakeVerb = new ItalianVerbConjugation(im);
        int notInDB=0,
                corresponding=0,
                irregular=0;
        HashMap<String,Integer> errors=new HashMap<>(200);
        for(String v:forms){
            if(!v.endsWith("are") && !v.endsWith("ere") && !v.endsWith("ire") )
                continue;
            ItalianVerbConjugation current = new ItalianVerbConjugation(im);
            current.setInfinitive(v);
            fakeVerb.setInfinitive("eee"+v);
            //now there are two verbs, one in the database (for at least one form) and the other not in it
            
            for(String mode:ItalianVerbConjugation.getImpersonalModes()){
                current.setMode(mode);
                fakeVerb.setMode(mode);
                String conjugated;
                try {
                    conjugated=current.getConjugated(false);
                } catch (ConjugationException e) {
                    notInDB++;
                    continue;
                }
                try {
                    if(fakeVerb.getConjugated(true).substring(3).equals(conjugated))
                        corresponding++;
                    else{
                        System.out.println("irregular form of "+v+" --> "+conjugated+ " ["+fakeVerb.getConjugated(true).substring(3)+"] "+mode);
                        irregular++;
                    }
                } catch (ConjugationException e) {
                    //should never happen
                    logger.error("error conjugating verb",e);
                }
                
            }
            for(String mode:ItalianVerbConjugation.getPersonalModes()){
                for(int person:new Integer[]{1,2,3}){
                    for(char num:new Character[]{'s','p'}){
                        String conjugated;
                        if(mode.equals("imperative") && person==1 && num=='s')
                            continue;
                        current.setMode(mode);
                        current.setNumber(num);
                        current.setPerson(person);
                        
                        fakeVerb.setMode(mode);
                        fakeVerb.setNumber(num);
                        fakeVerb.setPerson(person);
                        try {
                            conjugated=current.getConjugated(false);
                        } catch (ConjugationException e) {
                            notInDB++;
                            continue;
                        }
                        try {
                            if(fakeVerb.getConjugated(true).substring(3).equals(conjugated))
                                corresponding++;
                            else{
                                System.out.println("irregular form of "+v+" --> "+conjugated+ " ["+fakeVerb.getConjugated(true).substring(3)+"] "+mode+" "+person+" "+num);
                                errors.put(v.substring(v.length()-3)+" "+mode+" "+person+" "+num, 1+errors.getOrDefault(v.substring(v.length()-3)+" "+mode+" "+person+" "+num, 0));
                                irregular++;
                            }
                        } catch (ConjugationException e) {
                            //should never happen
                            logger.error("error conjugating verb",e);
                        }
                    }
                }
            }
            System.out.println(v+" "+notInDB+" unknown, "+corresponding+" corresponding, "+irregular+" irregular verbs so far ("+100.0*irregular/(irregular+corresponding)+"%)");
        }
        System.out.println("--------------\nirregular verb cases:\n");
        errors.entrySet().forEach(kv->System.out.println(kv.getKey()+"\t\t"+kv.getValue()));
        
    }
    
    
    private Connection connectionVerb,connectionPOS;
    
    //private POSTaggerME POStagger;
    private final ConcurrentLinkedQueue<POSTaggerME> posTaggers=new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<TokenizerME> tokenizers=new ConcurrentLinkedQueue<>();
    
    //private TokenizerME tokenizer;
    //private SentenceDetectorME sentencer;
    private final ConcurrentLinkedQueue<SentenceDetectorME> sentencers=new ConcurrentLinkedQueue<>();
    private final int concurrentInstances=200;
    
    /**
     * Load a model reading the data from the given folder
     * If the folder is null, it will look inside the resource folder
     *
     * @param modelFolder the folder containing the model binary files
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.io.FileNotFoundException */
    public ItalianModel(String modelFolder) throws ClassNotFoundException, SQLException, FileNotFoundException{
        //connect to the database for verb conjugations, hyponyms and POS tags
        Class.forName("org.sqlite.JDBC");
        if(modelFolder==null)
            modelFolder=ItalianModel.class.getResource("/").getPath();
        if(!new File(modelFolder+"/it_verb_model.db").isFile()){
            throw new FileNotFoundException("database "+modelFolder+"/it_verb_model.db not found or not a file");
        }
        connectionVerb = DriverManager.getConnection("jdbc:sqlite:"+modelFolder+"/it_verb_model.db");
        
        if(!new File(modelFolder+"/it_POS_model.db").isFile()){
            throw new FileNotFoundException("database "+modelFolder+"/it_POS_model.db not found or not a file");
        }
        connectionPOS = DriverManager.getConnection("jdbc:sqlite:"+modelFolder+"/it_POS_model.db");
        
        
        //load the tokener model for OpenNLP
        InputStream modelIn = new FileInputStream(modelFolder+"/it-token.bin");
        
        try {
            TokenizerModel model = new TokenizerModel(modelIn);
            for(int i=0;i<concurrentInstances;i++)
                tokenizers.add(new TokenizerME(model));
            //tokenizer = new TokenizerME(new TokenizerModel(modelIn));
        }
        catch (IOException e) {
            logger.error("error opening the tokener model",e);
        }
        finally {
            
            try {
                modelIn.close();
            }
            catch (IOException e) {
                logger.error("error closing the tokener model",e);
            }
            
        }
        //load the POS tagger model for OpenNLP
        modelIn = new FileInputStream(modelFolder+"/it-pos-maxent.bin");
        try {
            POSModel model = new POSModel(modelIn);
            for(int i=0;i<concurrentInstances;i++)
                posTaggers.add(new POSTaggerME(model));
            //this.POStagger = new POSTaggerME(new POSModel(modelIn));
            
        }
        catch (IOException e) {
            logger.error("error opening the PoS tagger model",e);
        }
        finally {
            try {
                modelIn.close();
            }
            catch (IOException e) {
                logger.error("error closing the poS tager model",e);
            }
        }
        
        //load the sentencer model for OpenNLP
        InputStream modelInSentence = new FileInputStream(modelFolder+"/it-sent.bin");
        try {
            SentenceModel model = new SentenceModel(modelInSentence);
            for(int i=0;i<concurrentInstances;i++)
                sentencers.add(new SentenceDetectorME(model));
            //sentencer=new SentenceDetectorME(new SentenceModel(modelInSentence));
        } catch (IOException e) {
            logger.error("error opening the sentencer model",e);
        }
        //load the list of stopWords
        FileReader sfr = new FileReader(modelFolder+"/stopwords.txt");
        BufferedReader sbr=new BufferedReader(sfr);
        String line;
        try {
            while((line=sbr.readLine())!=null)
                stopWords.add(line);
        } catch (IOException ex) {
            logger.error("error opening the stopwords list model",ex);
            
        }
    }
    
    
    public ItalianModel() throws ClassNotFoundException, FileNotFoundException, SQLException {
        this(null);
    }
    
    
    /**
     * Returns the possible verb conjugations corresponding to this verb.
     * @param word the verb to identify
     * @param forceIdentification whether or not to guess the verb form if not present in the database. If used, a non empty set is always returned
     * @return the possible verb conjugations as ItalianVerbConjugation instances
     * */
    public Set<ItalianVerbConjugation> getVerbs(String word, boolean forceIdentification){
        HashSet<ItalianVerbConjugation> res = new HashSet<>(4);
        try {
            PreparedStatement ps = connectionVerb.prepareStatement("SELECT * FROM verb_conjugations WHERE conjugated=?");
            ps.setString(1, word);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ItalianVerbConjugation ic=new ItalianVerbConjugation(this);
                ic.setMode(rs.getString("form"));
                ic.setInfinitive(rs.getString("infinitive"));
                ic.setPerson(rs.getInt("person"));
                ic.setNumber(rs.getString("number").charAt(0));
                ic.setConjugated(rs.getString("conjugated"));
                res.add(ic);
            }
            
        } catch (SQLException e) {
            logger.error("error loading data from the verb conjugation database",e);
        }
        if(!forceIdentification || res.size()>0)
            return res;
        else
            //no verb found, but we must force the identification, let's look for it
            return ItalianVerbConjugation.guessVerb(word,this);
    }
    
    /**
     * Returns the possible verb conjugations corresponding to this verb.
     * It doesn't force the identification, if the verb is not in the model, it returns an empty set
     * @param word the verb to identify
     * @return the possible verb conjugations as ItalianVerbConjugation instances
     *
     * */
    public Set<ItalianVerbConjugation> getVerbs(String word){
        return getVerbs(word, false);
    }
    
    protected String getVerbConjugation(String infinitive, String mode,
            char number, int person) {
        try {
            PreparedStatement ps = connectionVerb.prepareStatement("SELECT conjugated FROM verb_conjugations "
                    + "WHERE infinitive=? AND form=? AND number=? AND person=?");
            ps.setString(1, infinitive);
            ps.setString(2, mode);
            ps.setString(3, number+"");
            ps.setInt(4, person);
            
            ResultSet rs = ps.executeQuery();
            String longest="";
            while(rs.next()){
                if(rs.getString("conjugated").length()>longest.length())
                    longest=rs.getString("conjugated");
                else
                    if(rs.getString("conjugated").length()==longest.length()
                            && rs.getString("conjugated").hashCode()<longest.hashCode())
                        longest=rs.getString("conjugated");
            }
            if(longest.length()>0)
                return longest;
            
        } catch (SQLException e) {
            logger.error("error loading data from the verb conjugation database",e);
        }
        return null;
    }
    
    public String getVerbConjugation(String infinitive, String mode) {
        try {
            PreparedStatement ps=null;
            if(ItalianVerbConjugation.isImpersonalMode(mode)){
                ps = connectionVerb.prepareStatement("SELECT conjugated FROM verb_conjugations "
                        + "WHERE infinitive=? AND form=?");
            }
            else{
                throw new RuntimeException("requested a verb conjugation without a person and number, but "+mode+" is not an impersonal form");
            }
            ps.setString(1, infinitive);
            ps.setString(2, mode);
            
            ResultSet rs = ps.executeQuery();
            //look for the longest one with the hightst hash, as a criteria to be deterministic
            String longest="";
            while(rs.next()){
                if(rs.getString("conjugated").length()>longest.length())
                    longest=rs.getString("conjugated");
                else
                    if(rs.getString("conjugated").length()==longest.length()
                            && rs.getString("conjugated").hashCode()<longest.hashCode())
                        longest=rs.getString("conjugated");
            }
            if(longest.length()>0)
                return longest;
            
        } catch (SQLException e) {
            logger.error("error loading data from the verb conjugation database",e);
        }
        return null;
    }
    
    /**
     * Split a sentence into token and return the most ranked tag sequence using OpenNLP.
     * The dictionary of POS tags is not used
     *
     * @param sentence the String to tokenize
     * @return  the tokens found in the sentence, in the same order
     *
     */
    public String[] quickPOSTag(String sentence){
        TokenizerME tokenizer;
        //wait to get a tokenizer
        while((tokenizer= tokenizers.poll())==null)
            try {
                synchronized(tokenizers){
                    tokenizers.wait();
                }
            } catch (InterruptedException ex) {
                logger.error("interruption applying parallel tokening!",ex);
            }
        
        POSTaggerME POStagger;
        //wait to get a PoS tagger
        while((POStagger= posTaggers.poll())==null)
            try {
                synchronized(posTaggers){
                    posTaggers.wait();
                }
            } catch (InterruptedException ex) {
                logger.error("interruption applying parallel PoS-tagging!",ex);
            }
        String[] val = POStagger.tag(tokenizer.tokenize(sentence));
        
        //give back the tokenizer and the tagger
        tokenizers.add(tokenizer);
        synchronized(tokenizers){
            tokenizers.notify();
        }
        posTaggers.add(POStagger);
        synchronized(posTaggers){
            posTaggers.notify();
        }
        return val;
        
    }
    
    /**
     * Return the PoS values from en.wiktionary.
     * Those PoS are broader than the ones from getPoStags, and come from the parsing of a en.wiktionary dump.
     * This method is provided for who's interested in comparing the tags from the two sources.
     * @param word the word to classify
     * @return the PoS values from the parsing of an en.wiktionary dump, or an empty array in case of no matches
     */
    protected String[] getPoSvalues(String word){
        try {
            PreparedStatement ps = connectionPOS.prepareStatement("SELECT types FROM POS_tags WHERE word=?");
            ps.setString(1, word);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                return rs.getString("types").split(",");
            }
        } catch (SQLException e) {
            logger.error("error loading data from the verb PoS tags database",e);
        }
        return null;
    }
    
    /**
     * Split a string in tokens, returning them in an array
     * @param statement the text to tokenize
     * @return an array of the found tokens, as strings
     */
    public String[] tokenize(String statement){
        TokenizerME tokenizer;
        //wait to get a tokenizer
        while((tokenizer= tokenizers.poll())==null)
            try {
                synchronized(tokenizers){
                    tokenizers.wait();
                }
            } catch (InterruptedException ex) {
                logger.error("interruption applying parallel tokening!",ex);
            }
        String[] val = tokenizer.tokenize(statement);
        tokenizers.add(tokenizer);
        synchronized(tokenizers){
            tokenizers.notify();
        }
        return val;
        
    }
    
    
    /**
     * Tokenize and run POS tagging on the given text, returns an array of spans, each with the POS tag as the Span type
     *
     * @param text the text to tokenize and tag
     * @return  an array of Spans, each Span will have the identified PoS tag as type, that can be retrieved using getType() */
    public Span[] getPosTags(String text) {
        Span[] spans;
        Span[] returnMe = tagCache.getIfPresent(text);
        if(returnMe!=null)
            return returnMe;
        
        String[] tags;
        
        TokenizerME tokenizer;
        //wait to get a tokenizer
        while((tokenizer= tokenizers.poll())==null)
            try {
                synchronized(tokenizers){
                    tokenizers.wait();
                }
            } catch (InterruptedException ex) {
                logger.error("interruption applying parallel tokening!",ex);
            }
        
        POSTaggerME POStagger;
        //wait to get a PoS tagger
        while((POStagger= posTaggers.poll())==null)
            try {
                synchronized(posTaggers){
                    posTaggers.wait();
                }
            } catch (InterruptedException ex) {
                logger.error("interruption applying parallel PoS-tagging!",ex);
            }
        
        spans = tokenizer.tokenizePos(text);
        
        tags= POStagger.tag(Span.spansToStrings(spans, text));
        
        
        for(int i=0;i<spans.length;i++){
            spans[i]=new Span(spans[i].getStart(), spans[i].getEnd(), tags[i]);
        }
        
        tagCache.put(text, spans);
        
        
        //give back the tokenizer and the tagger
        tokenizers.add(tokenizer);
        synchronized(tokenizers){
            tokenizers.notify();
        }
        posTaggers.add(POStagger);
        synchronized(posTaggers){
            posTaggers.notify();
        }
        return spans;
    }
    
    
    /**
     * Return the tokens in this text as Span
     *
     * @param text the text to tokenize
     * @return an array of Span instances, marking the start and end positions of each token */
    public Span[] getTokens(String text) {
        TokenizerME tokenizer;
        //wait to get a tokenizer
        while((tokenizer= tokenizers.poll())==null)
            try {
                synchronized(tokenizers){
                    tokenizers.wait();
                }
            } catch (InterruptedException ex) {
                logger.error("interruption applying parallel tokening!",ex);
            }
        Span[] val = tokenizer.tokenizePos(text);
        tokenizers.add(tokenizer);
        synchronized(tokenizers){
            tokenizers.notify();
        }
        return val;
    }
    
    /**
     * Return the list of the infinitive verbs in the database, currently about 9K entries
     *
     */
    private Set<String> getAllKnownInfinitiveVerbs(){
        
        PreparedStatement ps;
        HashSet<String> res=new HashSet<>(500);
        try {
            ps = connectionVerb.prepareStatement("SELECT infinitive FROM verb_conjugations WHERE form='infinitive'");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                res.add(rs.getString("infinitive"));
            }
        } catch (SQLException e) {
            logger.error("error loading data from the verb conjugation database",e);
        }
        return res;
        
    }
    
    /**
     * Split a text in sentences, returning them as an array
     *
     * @param text the text to split in sentences
     * @return  an array of sentences*/
    public String[] getSentences(String text){
        SentenceDetectorME sentencer;
        //wait to get a tokenizer
        
        while((sentencer=sentencers.poll())==null)
            try {
                synchronized(sentencers){
                    sentencers.wait();
                }
            } catch (InterruptedException ex) {
                logger.error("interruption applying parallel sentencing!",ex);
            }
        String[] val = sentencer.sentDetect(text);
        sentencers.add(sentencer);
        synchronized(sentencers){
            sentencers.notify();
        }
        return val;
    }
    
    /**
     * Tells whether a word is an Italian stopword
     * @param token the word to examine
     * @return true if the word is a stopword
     */
    public boolean isStopWord(String token) {
        token=token.toLowerCase();
        return stopWords.contains(token);
    }
}
