package com.github.jacopofar.italib.numeric;

public class NumberMatcherIT {

	/**
	 * return the integer represented in the text, reading numbers in Italian.
	 * Example:
	 * "4" => 4
	 * "quattro" => 4
	 * if no value can be parsed, returns null
	 * */
	public static Integer parseNumber(String candidateNumber){
		//is this a number with digits?
		if(candidateNumber.matches("[-+]?([0-9])+")){
			return Integer.parseInt(candidateNumber.replace(" ", ""));
		}
		//TODO only numbers from 1 to 4, as a test
		if(candidateNumber.trim().equalsIgnoreCase("uno")) return 1;
		if(candidateNumber.trim().equalsIgnoreCase("due")) return 2;
		if(candidateNumber.trim().equalsIgnoreCase("tre")) return 3;
		if(candidateNumber.trim().equalsIgnoreCase("quattro")) return 4;
		return null;
		
	}
}
