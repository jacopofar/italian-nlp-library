package com.github.jacopofar.italib.restserver;

/**
 * Filter for the verb conjugations to be matched
 */
public class VerbFilter {
    String infinitive;

    public VerbFilter(String infinitive) {
        this.infinitive = infinitive;
    }

    public String getInfinitive() {
        return infinitive;
    }

    public void setInfinitive(String infinitive) {
        this.infinitive = infinitive;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getPerson() {
        return person;
    }

    public void setPerson(Integer person) {
        this.person = person;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    String mode;
    Integer person;
    String number;
}
