package com.github.jacopofar.italib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import com.github.jacopofar.italib.ItalianVerbConjugation.ConjugationException;
import com.github.jacopofar.italib.postagger.POSUtils;

/**
 * The main class to load an Italian language model and use it.
 * The model is based on Apache OpenNLP and the data extracted by com.github.jacopofar.conceptnetextractor
 * */
public class ItalianModel {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException {
		ItalianModel im = new ItalianModel();
		String stmt="Lo ha detto il premier Matteo Renzi al termine del vertice Ue, a Bruxelles, un incontro che nonostante tutte le incognite lo lascia soddisfatto: «Torniamo dall’Europa avendo vinto una battaglia di metodo e di sostanza», dice Renzi."
				+ "Il mio numero di telefono personale è +39 0268680762836 e non +39 5868 6867 2439";
		//String stmt="Il mio gatto Fuffi mangia i formaggini Mio con gusto";
		System.err.println(stmt);
		String[] tokens = im.tokenizer.tokenize(stmt);
		int p=0;
		for(String t:im.quickPOSTag(stmt)){
			System.out.println(tokens[p]+" "+t+":"+POSUtils.getDescription(t));
			p++;
		}
		System.out.println("-----");
		for(String t:tokens)
			System.out.println(t+":"+Arrays.deepToString(im.getPOSvalues(t)));
		String[] verbi = {"andavamo","mangerò","volare","correre","puffavo","googlare"};
		HashMap<String,String> people=new HashMap<String,String>(6);
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
						e.printStackTrace();
					}
				}
			}
		}
	}


	private Connection connectionVerb,connectionPOS;

	private POSTaggerME POStagger;
	private TokenizerME tokenizer;
	
	/**
	 * Load a model reading the data from the given folder
	 * If the folder is null, it will look inside the resource folder
	 * */
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
			tokenizer = new TokenizerME(new TokenizerModel(modelIn));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
		}
		//load the POS tagger model for OpenNLP
		modelIn = new FileInputStream(modelFolder+"/it-pos-maxent.bin");
		try {

			this.POStagger = new POSTaggerME(new POSModel(modelIn));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public ItalianModel() throws ClassNotFoundException, FileNotFoundException, SQLException {
		this(null);
	}


	/**
	 * Returns the possible verb conjugations corresponding to this verb.
	 * @param word the verb to identify
	 * @param forceIdentification whether or not to guess the verb form if not present in the database. If used, a non empty set is always returned
	 * */
	public Set<ItalianVerbConjugation> getVerbs(String word, boolean forceIdentification){
		HashSet<ItalianVerbConjugation> res = new HashSet<ItalianVerbConjugation>(4);
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
			e.printStackTrace();
		}
		if(!forceIdentification || res.size()>0)
			return res;
		else
			//no verb found, but we must force the identification, let's look for it
			return ItalianVerbConjugation.guessVerb(word,this);
	}

	/**
	 * Returns the possible verb conjugations corresponding to this verb.
	 * It doens't force the identification, if the verb is not in the model, it returns an empty set
	 * @param word the verb to identify
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
			e.printStackTrace();
		}
		return null;
	}

	public String getVerbConjugation(String infinitive, String mode) {
		try {
			PreparedStatement ps = connectionVerb.prepareStatement("SELECT conjugated FROM verb_conjugations "
					+ "WHERE infinitive=? AND form=?");
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
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Split a sentence into token and return the most ranked tag sequence using OpenNLP.
	 * The dictionary of POS tags is not used
	 * */
	public String[] quickPOSTag(String sentence){
		return POStagger.tag(tokenizer.tokenize(sentence));
	}

	protected String[] getPOSvalues(String word){
		try {
			PreparedStatement ps = connectionPOS.prepareStatement("SELECT types FROM POS_tags WHERE word=?");
			ps.setString(1, word);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				return rs.getString("types").split(",");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] tokenize(String statement){
		return this.tokenizer.tokenize(statement);
	}


	/**
	 * Tokenize and run POS tagging on the given text, returns an array of spans, each with the POS tag as the Span type
	 * */
	public Span[] getPosTags(String text) {
		Span[] spans = tokenizer.tokenizePos(text);
		String[] tags = POStagger.tag(Span.spansToStrings(spans, ""));
		for(int i=0;i<spans.length;i++){
			spans[i]=new Span(spans[i].getStart(), spans[i].getEnd(), tags[i]);
		}
		return spans;
	}

}
