package com.github.jacopofar.italib.postagger;

/**
 * Helper class to work with POS tags
 * */
public class POSUtils {
	/**
	 * Get a human readable description of a OpenNLP tag
	 * @param tag the tag (e.g.: "VAip3")
	 * @return a description, "UNKNOWN TAG" if the tag is unknown
	 * the list is from:
	 * https://github.com/aciapetti/opennlp-italian-models/blob/master/lang/it/POS/tagsDictionaryIt.txt
	 * */
	public static String getDescription(String tag){
		if(tag.equals("B")) return "adverb";
		if(tag.equals("BN")) return "negation adverb";
		if(tag.equals("CC")) return "coordinate conjunction";
		if(tag.equals("CS")) return "subordinate conjunction";
		if(tag.equals("DD")) return "demonstrative determiner";
		if(tag.equals("DE")) return "exclamative determiner";
		if(tag.equals("DI")) return "indefinite determiner";
		if(tag.equals("DQ")) return "interrogative determiner";
		if(tag.equals("DR")) return "relative determiner";
		if(tag.equals("E")) return "preposition";
		if(tag.equals("EA")) return "articulated preposition";
		if(tag.equals("FB")) return "balanced punctuation (round brackets, double quotes, etc.)";
		if(tag.equals("FC")) return "clause boundary punctuation (. - : ;)";
		if(tag.equals("FF")) return "comma, dash, omissis (, ... -)";
		if(tag.equals("FS")) return "sentence boundary punctuation (. ? ! ...)";
		if(tag.equals("I")) return "interjection";
		if(tag.equals("N")) return "cardinal number";
		if(tag.equals("PC")) return "clitic pronoun";
		if(tag.equals("PD")) return "demonstrative pronoun";
		if(tag.equals("PE")) return "personal pronoun";
		if(tag.equals("PI")) return "indefinite pronoun";
		if(tag.equals("PP")) return "possessive pronoun";
		if(tag.equals("PQ")) return "interrogative pronoun";
		if(tag.equals("PR")) return "relative pronoun";
		if(tag.equals("RD")) return "determinative article";
		if(tag.equals("RI")) return "indeterminative article";
		if(tag.equals("T")) return "predeterminer";
		if(tag.equals("SA")) return "abbreviation";
		if(tag.equals("SP")) return "proper noun";
		if(tag.equals("XH")) return "hashtag twitter (#nlp)";
		if(tag.equals("XM")) return "twitter mentions (@obama)";
		if(tag.equals("XE")) return "Emoticon (smiley :-))";
		if(tag.equals("XX")) return "Others (formula, , not classified words, other alphabetic symbols, etc.)";
		if(tag.equals("Ss")) return "S num=s singular noun";
		if(tag.equals("Sp")) return "S num=p plural noun";
		if(tag.equals("Sn")) return "S num=n underspecified noun";
		if(tag.equals("As")) return "A num=s singular adjective";
		if(tag.equals("Ap")) return "A num=p plural adjective";
		if(tag.equals("An")) return "A num=n underspecified adjective";
		if(tag.equals("APs")) return "AP num=s singular possessive adjective";
		if(tag.equals("APp")) return "AP num=p plural possessive adjective";
		if(tag.equals("APn")) return "AP num=n underspecified possessive adjective";
		if(tag.equals("NOs")) return "NO num=s singular ordinal number";
		if(tag.equals("NOp")) return "NO num=p plural ordinal number";
		if(tag.equals("NOn")) return "NO num=n underspecified ordinal number";
		if(tag.equals("SWs")) return "SW num=s singular foreign name";
		if(tag.equals("SWp")) return "SW num=p plural foreign name";
		if(tag.equals("SWn")) return "SW num=n underspecified foreign name";
		if(tag.equals("Vip")) return "V mod=i ten=p per!=3 main verb indicative present, other than 3° person";
		if(tag.equals("Vip3")) return "V mod=i ten=p per=3 main verb indicative present, 3° person";
		if(tag.equals("Vii")) return "V mod=i ten=i per!=3 main verb indicative imperfect, other than 3° person";
		if(tag.equals("Vii3")) return "V mod=i ten=i per=3 main verb indicative imperfect, 3° person";
		if(tag.equals("Vis")) return "V mod=i ten=s per!=3 main verb indicative past, other than 3° person";
		if(tag.equals("Vis3")) return "V mod=i ten=s per=3 main verb indicative past, 3° person";
		if(tag.equals("Vif")) return "V mod=i ten=f per!=3 main verb indicative future, other than 3° person";
		if(tag.equals("Vif3")) return "V mod=i ten=f per=3 main verb indicative future, 3° person";
		if(tag.equals("Vcp")) return "V mod=c ten=p per!=3 main verb conjunctive present, other than 3° person";
		if(tag.equals("Vcp3")) return "V mod=c ten=p per=3 main verb conjunctive present, 3° person";
		if(tag.equals("Vci")) return "V mod=c ten=i per!=3 main verb conjunctive imperfect, other than 3° person";
		if(tag.equals("Vci3")) return "V mod=c ten=i per=3 main verb conjunctive imperfect, 3° person";
		if(tag.equals("Vdp")) return "V mod=d ten=p per!=3 main verb conditional present, other than 3° person";
		if(tag.equals("Vdp3")) return "V mod=d ten=p per=3 main verb conditional present, 3° person";
		if(tag.equals("Vg")) return "V mod=g main verb gerundive";
		if(tag.equals("Vp")) return "V mod=p main verb participle";
		if(tag.equals("Vf")) return "V mod=f main verb infinite";
		if(tag.equals("Vm")) return "V mod=m main verb imperative";
		if(tag.equals("VAip")) return "VA mod=i ten=p per!=3 auxiliary verb indicative present, other than 3° person";
		if(tag.equals("VAip3")) return "VA mod=i ten=p per=3 auxiliary verb indicative present, 3° person";
		if(tag.equals("VAii")) return "VA mod=i ten=i per!=3 auxiliary verb indicative imperfect, other than 3° person";
		if(tag.equals("VAii3")) return "VA mod=i ten=i per=3 auxiliary verb indicative imperfect, 3° person";
		if(tag.equals("VAis")) return "VA mod=i ten=s per!=3 auxiliary verb indicative past, other than 3° person";
		if(tag.equals("Vis3")) return "VA mod=i ten=s per=3 auxiliary verb indicative past, 3° person";
		if(tag.equals("VAif")) return "VA mod=i ten=f per!=3 auxiliary verb indicative future, other than 3° person";
		if(tag.equals("VAif3")) return "VA mod=i ten=f per=3 auxiliary verb indicative future, 3° person";
		if(tag.equals("VAcp")) return "VA mod=c ten=p per!=3 auxiliary verb conjunctive present, other than 3° person";
		if(tag.equals("VAcp3")) return "VA mod=c ten=p per=3 auxiliary verb conjunctive present, 3° person";
		if(tag.equals("VAci")) return "VA mod=c ten=i per!=3 auxiliary verb conjunctive imperfect, other than 3° person";
		if(tag.equals("VAci3")) return "VA mod=c ten=i per=3 auxiliary verb conjunctive imperfect, 3° person";
		if(tag.equals("VAdp")) return "VA mod=d ten=p per!=3 auxiliary verb conditional present, other than 3° person";
		if(tag.equals("VAdp3")) return "VA mod=d ten=p per=3 auxiliary verb conditional present, 3° person";
		if(tag.equals("VAg")) return "VA mod=g auxiliary verb gerundive";
		if(tag.equals("VAp")) return "VA mod=p auxiliary verb participle";
		if(tag.equals("VAf")) return "VA mod=f auxiliary verb infinite";
		if(tag.equals("VAm")) return "VA mod=m auxiliary verb imperative";
		if(tag.equals("VMip")) return "VM mod=i ten=p per!=3 modal verb indicative present, other than 3° person";
		if(tag.equals("VMip3")) return "VM mod=i ten=p per=3 modal verb indicative present, 3° person";
		if(tag.equals("VMii")) return "VM mod=i ten=i per!=3 modal verb indicative imperfect, other than 3° person";
		if(tag.equals("VMii3")) return "VM mod=i ten=i per=3 modal verb indicative imperfect, 3° person";
		if(tag.equals("VMis")) return "VM mod=i ten=s per!=3 modal verb indicative past, other than 3° person";
		if(tag.equals("VMis3")) return "VM mod=i ten=s per=3 modal verb indicative past, 3° person";
		if(tag.equals("VMif")) return "VM mod=i ten=f per!=3 modal verb indicative future, other than 3° person";
		if(tag.equals("VMif3")) return "VM mod=i ten=f per=3 modal verb indicative future, 3° person";
		if(tag.equals("VMcp")) return "VM mod=c ten=p per!=3 modal verb conjunctive present, other than 3° person";
		if(tag.equals("VMcp3")) return "VM mod=c ten=p per=3 modal verb conjunctive present, 3° person";
		if(tag.equals("VMci")) return "VM mod=c ten=i per!=3 modal verb conjunctive imperfect, other than 3° person";
		if(tag.equals("VMci3")) return "VM mod=c ten=i per=3 modal verb conjunctive imperfect, 3° person";
		if(tag.equals("VMdp")) return "VM mod=d ten=p per!=3 modal verb conditional present, other than 3° person";
		if(tag.equals("VMdp3")) return "VM mod=d ten=p per=3 modal verb conditional present, 3° person";
		if(tag.equals("VMg")) return "VM mod=g modal verb gerundive";
		if(tag.equals("VMp")) return "VM mod=p modal verb participle";
		if(tag.equals("VMf")) return "VM mod=f modal verb infinite";
		if(tag.equals("VMm")) return "VM mod=m modal verb imperative";

		return "UNKNOWN TAG";
	}
}
