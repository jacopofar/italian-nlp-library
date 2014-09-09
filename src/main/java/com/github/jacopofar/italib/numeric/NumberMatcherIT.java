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
package com.github.jacopofar.italib.numeric;

public class NumberMatcherIT {
    
    /**
     * return the integer represented in the text, reading numbers in Italian.
     * Example:
     * "4" => 4
     * "quattro" => 4
     * if no value can be parsed, returns null
     *
     * @param candidateNumber the number to parse
     * @return  the identified number, or null if no number could be found*/
    public static Integer parseNumber(String candidateNumber){
        //is this a number with digits?
        if(candidateNumber.matches("[-+]?([0-9])+")){
            return Integer.parseInt(candidateNumber.replace(" ", ""));
        }
        candidateNumber=candidateNumber.trim().toLowerCase();
        //TODO to be improved, someday
        if(candidateNumber.equals("uno")) return 1;
        if(candidateNumber.equals("due")) return 2;
        if(candidateNumber.equals("tre")) return 3;
        if(candidateNumber.equals("quattro")) return 4;
        if(candidateNumber.equals("cinque")) return 5;
        if(candidateNumber.equals("sei")) return 6;
        if(candidateNumber.equals("sette")) return 7;
        if(candidateNumber.equals("otto")) return 8;
        if(candidateNumber.equals("nove")) return 9;
        if(candidateNumber.equals("dieci")) return 10;
        if(candidateNumber.equals("undici")) return 11;
        if(candidateNumber.equals("dodici")) return 12;
        if(candidateNumber.equals("tredici")) return 13;
        if(candidateNumber.equals("quattordici")) return 14;
        if(candidateNumber.equals("quindici")) return 15;
        if(candidateNumber.equals("venti")) return 20;
        if(candidateNumber.equals("trenta")) return 30;
        if(candidateNumber.equals("quaranta")) return 40;
        if(candidateNumber.equals("cinquanta")) return 50;
        if(candidateNumber.equals("sessanta")) return 60;
        if(candidateNumber.equals("settanta")) return 70;
        if(candidateNumber.equals("ottanta")) return 80;
        if(candidateNumber.equals("novanta")) return 90;
        if(candidateNumber.equals("cento")) return 100;
        if(candidateNumber.equals("duecento")) return 200;
        if(candidateNumber.equals("mille")) return 1000;
        if(candidateNumber.equals("diecimila")) return 10000;
        if(candidateNumber.equals("un milione")) return 1000000;
        if(candidateNumber.equals("un paio")) return 2;
        if(candidateNumber.equals("una dozzina")) return 12;
        
        return null;
        
    }
}
