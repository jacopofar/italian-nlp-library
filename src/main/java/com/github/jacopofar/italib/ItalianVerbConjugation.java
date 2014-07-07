package com.github.jacopofar.italib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Represents an Italian verb conjugation and provide methods to change the form, person and number according to a model for Italian
 * */
public class ItalianVerbConjugation {
	@SuppressWarnings("serial")
	public class ConjugationException extends Exception {

		private String message;

		public ConjugationException(String msg) {
			this.message=msg;
		}
		public void printStackTrace(){
			super.printStackTrace();
			System.err.println("conjugation error: "+message);
		}


	}

	public enum Mode {
		INFINITIVE,
		GERUND,
		PRESENT_PARTICIPLE,
		PAST_PARTICIPLE,
		INDICATIVE_PRESENT,
		INDICATIVE_IMPERFECT,
		INDICATIVE_PAST_HISTORIC,
		INDICATIVE_FUTURE,
		CONDITIONAL_PRESENT,
		SUBJUNCTIVE_PRESENT,
		SUBJUNCTIVE_IMPERFECT,
		IMPERATIVE
	}
	private String conjugated;
	private String infinitive;
	private static HashMap<String,String> defaultSuffixes= new HashMap<String,String>(100);
	private char number='-';
	private int person=0;
	private Mode mode;
	private ItalianModel model;
	private static HashMap<Mode,String> modeRepresentations;
	static{
		modeRepresentations=new HashMap<Mode,String>(11);
		modeRepresentations.put(Mode.INFINITIVE,"infinitive");
		modeRepresentations.put(Mode.IMPERATIVE,"imperative");
		modeRepresentations.put(Mode.GERUND,"gerund");
		modeRepresentations.put(Mode.PRESENT_PARTICIPLE,"present participle");
		modeRepresentations.put(Mode.PAST_PARTICIPLE,"past participle");
		modeRepresentations.put(Mode.INDICATIVE_PRESENT,"indicative present");
		modeRepresentations.put(Mode.INDICATIVE_IMPERFECT,"indicative imperfect");
		modeRepresentations.put(Mode.INDICATIVE_PAST_HISTORIC,"indicative past historic");
		modeRepresentations.put(Mode.INDICATIVE_FUTURE,"indicative future");
		modeRepresentations.put(Mode.CONDITIONAL_PRESENT,"conditional");
		modeRepresentations.put(Mode.SUBJUNCTIVE_PRESENT,"subjunctive present");
		modeRepresentations.put(Mode.SUBJUNCTIVE_IMPERFECT,"subjunctive imperfect");

		defaultSuffixes.put("are,conditional,1,p","eremmo");
		defaultSuffixes.put("are,conditional,1,s","erei");
		defaultSuffixes.put("are,conditional,2,p","ereste");
		defaultSuffixes.put("are,conditional,2,s","eresti");
		defaultSuffixes.put("are,conditional,3,p","erebbero");
		defaultSuffixes.put("are,conditional,3,s","erebbe");
		defaultSuffixes.put("are,gerund","ando");
		defaultSuffixes.put("are,imperative,1,p","iamo");
		defaultSuffixes.put("are,imperative,2,p","ate");
		defaultSuffixes.put("are,imperative,2,s","a");
		defaultSuffixes.put("are,imperative,3,p","ino");
		defaultSuffixes.put("are,imperative,3,s","i");
		defaultSuffixes.put("are,indicative future,1,p","eremo");
		defaultSuffixes.put("are,indicative future,1,s","erò");
		defaultSuffixes.put("are,indicative future,2,p","erete");
		defaultSuffixes.put("are,indicative future,2,s","erai");
		defaultSuffixes.put("are,indicative future,3,p","eranno");
		defaultSuffixes.put("are,indicative future,3,s","erà");
		defaultSuffixes.put("are,indicative imperfect,1,p","avamo");
		defaultSuffixes.put("are,indicative imperfect,1,s","avo");
		defaultSuffixes.put("are,indicative imperfect,2,p","avate");
		defaultSuffixes.put("are,indicative imperfect,2,s","avi");
		defaultSuffixes.put("are,indicative imperfect,3,p","avano");
		defaultSuffixes.put("are,indicative imperfect,3,s","ava");
		defaultSuffixes.put("are,indicative past historic,1,p","ammo");
		defaultSuffixes.put("are,indicative past historic,1,s","ai");
		defaultSuffixes.put("are,indicative past historic,2,p","aste");
		defaultSuffixes.put("are,indicative past historic,2,s","asti");
		defaultSuffixes.put("are,indicative past historic,3,p","arono");
		defaultSuffixes.put("are,indicative past historic,3,s","ò");
		defaultSuffixes.put("are,indicative present,1,p","iamo");
		defaultSuffixes.put("are,indicative present,1,s","o");
		defaultSuffixes.put("are,indicative present,2,p","ate");
		defaultSuffixes.put("are,indicative present,2,s","i");
		defaultSuffixes.put("are,indicative present,3,p","ano");
		defaultSuffixes.put("are,indicative present,3,s","a");
		defaultSuffixes.put("are,infinitive","are");
		defaultSuffixes.put("are,past participle","ato");
		defaultSuffixes.put("are,present participle","ante");
		defaultSuffixes.put("are,subjunctive imperfect,1,p","assimo");
		defaultSuffixes.put("are,subjunctive imperfect,1,s","assi");
		defaultSuffixes.put("are,subjunctive imperfect,2,p","aste");
		defaultSuffixes.put("are,subjunctive imperfect,2,s","assi");
		defaultSuffixes.put("are,subjunctive imperfect,3,p","assero");
		defaultSuffixes.put("are,subjunctive imperfect,3,s","asse");
		defaultSuffixes.put("are,subjunctive present,1,p","iamo");
		defaultSuffixes.put("are,subjunctive present,1,s","i");
		defaultSuffixes.put("are,subjunctive present,2,p","iate");
		defaultSuffixes.put("are,subjunctive present,2,s","i");
		defaultSuffixes.put("are,subjunctive present,3,p","ino");
		defaultSuffixes.put("are,subjunctive present,3,s","i");
		defaultSuffixes.put("ere,conditional,1,p","eremmo");
		defaultSuffixes.put("ere,conditional,1,s","erei");
		defaultSuffixes.put("ere,conditional,2,p","ereste");
		defaultSuffixes.put("ere,conditional,2,s","eresti");
		defaultSuffixes.put("ere,conditional,3,p","erebbero");
		defaultSuffixes.put("ere,conditional,3,s","erebbe");
		defaultSuffixes.put("ere,gerund","endo");
		defaultSuffixes.put("ere,imperative,1,p","iamo");
		defaultSuffixes.put("ere,imperative,2,p","ete");
		defaultSuffixes.put("ere,imperative,2,s","i");
		defaultSuffixes.put("ere,imperative,3,p","ano");
		defaultSuffixes.put("ere,imperative,3,s","a");
		defaultSuffixes.put("ere,indicative future,1,p","eremo");
		defaultSuffixes.put("ere,indicative future,1,s","erò");
		defaultSuffixes.put("ere,indicative future,2,s","erai");
		defaultSuffixes.put("ere,indicative future,2,p","erete");
		defaultSuffixes.put("ere,indicative future,3,s","erà");
		defaultSuffixes.put("ere,indicative future,3,p","eranno");
		defaultSuffixes.put("ere,indicative imperfect,1,p","evamo");
		defaultSuffixes.put("ere,indicative imperfect,1,s","evo");
		defaultSuffixes.put("ere,indicative imperfect,2,p","evate");
		defaultSuffixes.put("ere,indicative imperfect,2,s","evi");
		defaultSuffixes.put("ere,indicative imperfect,3,p","evano");
		defaultSuffixes.put("ere,indicative imperfect,3,s","eva");
		defaultSuffixes.put("ere,indicative past historic,1,p","emmo");
		defaultSuffixes.put("ere,indicative past historic,1,s","ei");
		defaultSuffixes.put("ere,indicative past historic,2,p","este");
		defaultSuffixes.put("ere,indicative past historic,2,s","esti");
		defaultSuffixes.put("ere,indicative past historic,3,p","erono");
		defaultSuffixes.put("ere,indicative past historic,3,s","ette");
		defaultSuffixes.put("ere,indicative present,1,p","iamo");
		defaultSuffixes.put("ere,indicative present,1,s","o");
		defaultSuffixes.put("ere,indicative present,2,p","ete");
		defaultSuffixes.put("ere,indicative present,2,s","i");
		defaultSuffixes.put("ere,indicative present,3,p","ono");
		defaultSuffixes.put("ere,indicative present,3,s","e");
		defaultSuffixes.put("ere,infinitive","ere");
		defaultSuffixes.put("ere,past participle","ito");
		defaultSuffixes.put("ere,present participle","ente");
		defaultSuffixes.put("ere,subjunctive imperfect,1,p","essimo");
		defaultSuffixes.put("ere,subjunctive imperfect,1,s","essi");
		defaultSuffixes.put("ere,subjunctive imperfect,2,p","este");
		defaultSuffixes.put("ere,subjunctive imperfect,2,s","essi");
		defaultSuffixes.put("ere,subjunctive imperfect,3,p","essero");
		defaultSuffixes.put("ere,subjunctive imperfect,3,s","esse");
		defaultSuffixes.put("ere,subjunctive present,1,p","iamo");
		defaultSuffixes.put("ere,subjunctive present,1,s","a");
		defaultSuffixes.put("ere,subjunctive present,2,p","essimo");
		defaultSuffixes.put("ere,subjunctive present,2,s","iate");
		defaultSuffixes.put("ere,subjunctive present,3,p","ano");
		defaultSuffixes.put("ere,subjunctive present,3,s","i");
		defaultSuffixes.put("ire,conditional,1,p","iremmo");
		defaultSuffixes.put("ire,conditional,1,s","irei");
		defaultSuffixes.put("ire,conditional,2,p","ireste");
		defaultSuffixes.put("ire,conditional,2,s","iresti");
		defaultSuffixes.put("ire,conditional,3,p","irebbero");
		defaultSuffixes.put("ire,conditional,3,s","irebbe");
		defaultSuffixes.put("ire,gerund","endo");
		defaultSuffixes.put("ire,imperative,1,p","iamo");
		defaultSuffixes.put("ire,imperative,2,s","isci");
		defaultSuffixes.put("ire,imperative,2,p","ite");
		defaultSuffixes.put("ire,imperative,3,p","ite");
		defaultSuffixes.put("ire,imperative,3,s","isca");
		defaultSuffixes.put("ire,indicative future,1,p","iremo");
		defaultSuffixes.put("ire,indicative future,1,s","irò");
		defaultSuffixes.put("ire,indicative future,2,s","irai");
		defaultSuffixes.put("ire,indicative future,2,p","irete");
		defaultSuffixes.put("ire,indicative future,3,s","irà");
		defaultSuffixes.put("ire,indicative future,3,p","iranno");

		defaultSuffixes.put("ire,indicative imperfect,1,p","ivamo");
		defaultSuffixes.put("ire,indicative imperfect,1,s","ivo");
		defaultSuffixes.put("ire,indicative imperfect,2,p","ivate");
		defaultSuffixes.put("ire,indicative imperfect,2,s","ivi");
		defaultSuffixes.put("ire,indicative imperfect,3,p","ivano");
		defaultSuffixes.put("ire,indicative imperfect,3,s","iva");
		defaultSuffixes.put("ire,indicative past historic,1,p","immo");
		defaultSuffixes.put("ire,indicative past historic,1,s","ii");
		defaultSuffixes.put("ire,indicative past historic,2,p","iste");
		defaultSuffixes.put("ire,indicative past historic,2,s","isti");
		defaultSuffixes.put("ire,indicative past historic,3,p","irono");
		defaultSuffixes.put("ire,indicative past historic,3,s","ì");
		defaultSuffixes.put("ire,indicative present,1,p","iamo");
		defaultSuffixes.put("ire,indicative present,1,s","isco");
		defaultSuffixes.put("ire,indicative present,2,p","ite");
		defaultSuffixes.put("ire,indicative present,2,s","isci");
		defaultSuffixes.put("ire,indicative present,3,p","ono");
		defaultSuffixes.put("ire,indicative present,3,s","isce");
		defaultSuffixes.put("ire,infinitive","ire");
		defaultSuffixes.put("ire,past participle","uto");
		defaultSuffixes.put("ire,present participle","ente");
		defaultSuffixes.put("ire,subjunctive imperfect,1,p","issimo");
		defaultSuffixes.put("ire,subjunctive imperfect,1,s","issi");
		defaultSuffixes.put("ire,subjunctive imperfect,2,p","iste");
		defaultSuffixes.put("ire,subjunctive imperfect,2,s","issi");
		defaultSuffixes.put("ire,subjunctive imperfect,3,p","issero");
		defaultSuffixes.put("ire,subjunctive imperfect,3,s","isse");
		defaultSuffixes.put("ire,subjunctive present,1,p","iamo");
		defaultSuffixes.put("ire,subjunctive present,1,s","isca");
		defaultSuffixes.put("ire,subjunctive present,2,p","iate");
		defaultSuffixes.put("ire,subjunctive present,2,s","isca");
		defaultSuffixes.put("ire,subjunctive present,3,p","iscano");
		defaultSuffixes.put("ire,subjunctive present,3,s","isca");



	}

	public static Set<String> getImpersonalModes(){
		HashSet<String> res=new HashSet<String>();
		res.add(modeRepresentations.get(Mode.GERUND));
		res.add(modeRepresentations.get(Mode.PAST_PARTICIPLE));
		res.add(modeRepresentations.get(Mode.PRESENT_PARTICIPLE));
		res.add(modeRepresentations.get(Mode.INFINITIVE));
		return res;
	}

	public static Set<String> getPersonalModes() {
		HashSet<String> res=new HashSet<String>();


		res.add(modeRepresentations.get(Mode.INDICATIVE_PRESENT));
		res.add(modeRepresentations.get(Mode.INDICATIVE_IMPERFECT));
		res.add(modeRepresentations.get(Mode.INDICATIVE_PAST_HISTORIC));
		res.add(modeRepresentations.get(Mode.INDICATIVE_FUTURE));
		res.add(modeRepresentations.get(Mode.CONDITIONAL_PRESENT));
		res.add(modeRepresentations.get(Mode.SUBJUNCTIVE_PRESENT));
		res.add(modeRepresentations.get(Mode.SUBJUNCTIVE_IMPERFECT));
		res.add(modeRepresentations.get(Mode.IMPERATIVE));
		return res;
	}

	/**
	 * Instantiates a verb conjugation object starting from the string representation from the toStringRepresentation method
	 * */
	public ItalianVerbConjugation(String line, String delimiter) {
		try{
			String[] data = line.split(delimiter);
			this.conjugated=data[0];
			this.infinitive=data[1];
			this.setMode(data[2]);
			this.number=data[3].charAt(0);
			this.person=Integer.parseInt(data[4]);
		}catch(Exception e){
			System.err.println("Offending verb string: "+line);
			e.printStackTrace();
			throw new RuntimeException("Error while instantiating the verb object");
		}
	}

	public ItalianVerbConjugation(ItalianModel italianModel) {
		this.model=italianModel;
	}
	public String getConjugated() throws ConjugationException {
		return getConjugated(true);
	}

	public String getConjugated(boolean guess) throws ConjugationException {
		if(infinitive==null)
			throw new ConjugationException("Infinitive not set, nothing to conjugate");
		if(conjugated!=null)
			return conjugated;
		//impersonal mode? retrieve it
		if(mode==Mode.GERUND || mode==Mode.PAST_PARTICIPLE || mode==Mode.PRESENT_PARTICIPLE || mode==Mode.INFINITIVE){
			this.conjugated=model.getVerbConjugation(infinitive, this.getMode());
			if(this.conjugated==null){
				if(!guess)
					throw new ConjugationException("conjugation not in the database, guessing disabled");
				String defaultSuffix=ItalianVerbConjugation.defaultSuffixes.get(this.infinitive.substring(this.infinitive.length()-3,this.infinitive.length())+","+this.getMode());
				if(defaultSuffix==null)
					throw new ConjugationException("conjugation unknown for the verb "+infinitive+" and mode "+this.getMode());
				this.conjugated=this.infinitive.substring(0,this.infinitive.length()-3)+defaultSuffix;
			}
		}
		else{
			//is the person a valid one?
			if(this.person<1 || this.person>3)
				throw new ConjugationException("person "+this.person+" not valid, have to be 1,2 or 3 since the mode is "+this.getMode());
			//imperative doesn't allow for first singular person
			if(this.person==1 && this.number=='s' && this.getMode().equals("imperative"))
				throw new ConjugationException("the Italian imperative form does not have the first singular person (you can't give orders to yourself!)");

			//is the number a valid one?
			if(this.number!='s' && this.number!='p')
				throw new ConjugationException("number "+this.person+" not valid, have to be 's' or 'p' since the mode is "+this.getMode());
			//let's try to retrieve the verb using the model
			this.conjugated=model.getVerbConjugation(this.infinitive, this.getMode(), this.number, this.person);
			if(this.conjugated==null){
				if(!guess)
					throw new ConjugationException("conjugation not in the database, guessing disabled");
				String searchFor=this.infinitive.substring(this.infinitive.length()-3,this.infinitive.length())+","+this.getMode()+","+this.person+","+this.number;
				String defaultSuffix=defaultSuffixes.get(searchFor);
				if(defaultSuffix==null)
					throw new ConjugationException("conjugation unknown for the verb "+infinitive+" and mode "+this.getMode()+" person "+this.person+" number "+this.number+ " (search string:'"+searchFor+"')");
				String root=this.infinitive.substring(0,this.infinitive.length()-3);
				//if the infinitive contains "ci" or "gi" before the ending, change the conjugations
				//for example "mangiare" becomes "mangerò" not "mangierò"
				if((root.endsWith("ci")|| root.endsWith("gi"))
						&&((defaultSuffix.startsWith("e") || (defaultSuffix.startsWith("i")) && !root.endsWith("cci") && !root.endsWith("ggi")))){
					this.conjugated=root.substring(0,root.length()-1)+defaultSuffix;
				}

				//remove the double i
				if(root.endsWith("i")&&(defaultSuffix.startsWith("i"))){
					this.conjugated=root.substring(0,root.length()-1)+defaultSuffix;
				}

				//"rinfreschi", not "rinfresci"
				if(root.endsWith("c")&&(defaultSuffix.startsWith("e") || defaultSuffix.startsWith("i"))){
					this.conjugated=root.substring(0,root.length())+"h"+defaultSuffix;
				}

				//"collocheremmo", not "colloceremmo"
				if(root.endsWith("c")&&(defaultSuffix.startsWith("e"))){
					this.conjugated=root.substring(0,root.length())+"h"+defaultSuffix;
				}


				//if none of the above was applied, use the default suffix
				if(this.conjugated==null){
					this.conjugated=root+defaultSuffix;
				}
			}
		}
		return this.conjugated;
	}
	public void setConjugated(String conjugated) {
		this.conjugated = conjugated;
	}
	public String getInfinitive() {

		return infinitive;
	}
	public void setInfinitive(String infinitive) {
		this.conjugated=null;
		this.infinitive = infinitive;
	}
	public char getNumber() {
		return number;
	}
	public void setNumber(char number) {
		this.conjugated=null;
		this.number = number;
	}
	public int getPerson() {
		return person;
	}
	public void setPerson(int person) {
		this.conjugated=null;
		this.person = person;
	}

	public String toStringRepresentation(String delim) {
		return this.conjugated+delim+
				this.infinitive+delim+
				this.getMode()+delim+
				this.number+delim+
				this.person;
	}
	/**
	 * Returns the mode and the time of the conjugation, for example "indicative past historic" 
	 * */
	public String getMode() {
		return modeRepresentations.get(mode);
	}
	public void setMode(String repr) {
		for(Entry<Mode, String> mr:modeRepresentations.entrySet()){
			if(mr.getValue().equals(repr)){
				this.mode= mr.getKey();
				return;
			}
		}
		throw new RuntimeException("ERROR, the verbal mode '"+repr+"' is unknown");
	}

	public String toString(){
		return this.toStringRepresentation(",");
	}

	public static Set<ItalianVerbConjugation> guessVerb(String word,
			ItalianModel italianModel) {
		HashSet<ItalianVerbConjugation> res = new HashSet<ItalianVerbConjugation>(4);

		for(Entry<String, String> ds:defaultSuffixes.entrySet()){
			if(word.endsWith(ds.getValue())){
				ItalianVerbConjugation ic = new ItalianVerbConjugation(italianModel);

				String[] params = ds.getKey().split(",");
				ic.infinitive=word.substring(0,word.length()-ds.getValue().length())+params[0];
				ic.setMode(params[1]);
				if(params.length==4){
					ic.setNumber(params[3].charAt(0));
					ic.setPerson(Integer.parseInt(params[2]));
				}
				ic.setConjugated(word);
				res.add(ic);
			}
		}
		if(res.size()==0){
			ItalianVerbConjugation ic = new ItalianVerbConjugation(italianModel);
			ic.setConjugated(word+"are");
			ic.infinitive=word+"are";
			ic.setMode("infinitive");
			res.add(ic);
		}
		return res;
	}

	public int hashCode(){
		return this.toString().hashCode();
	}

	public static boolean isImpersonalMode(String mode2) {
		for(Entry<Mode, String> mr:modeRepresentations.entrySet()){
			if(mr.getValue().equals(mode2)){
				Mode mode=mr.getKey();
				if(mode==Mode.GERUND || mode==Mode.PAST_PARTICIPLE || mode==Mode.PRESENT_PARTICIPLE || mode==Mode.INFINITIVE)
					return true;
				else return false;
			}
		}
		throw new RuntimeException("ERROR, the verbal mode '"+mode2+"' is unknown");
	}

	public String toJSON() {
		return "{'infinitive':'"+this.infinitive+"'"
				+ ",'mode':'"+this.getMode()+"'"
				+ (this.number==0?"":(",'number':'"+this.getNumber()+"'"))
				+ (this.person==0?"":(",'person':'"+this.getPerson()+"'"))
				+ "}";
	}

}
