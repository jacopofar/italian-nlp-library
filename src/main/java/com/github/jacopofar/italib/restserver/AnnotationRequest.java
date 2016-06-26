package com.github.jacopofar.italib.restserver;

import java.util.LinkedList;

/**
 * Created on 2016-06-26.
 */
public class AnnotationRequest {
    String parameter;
    String text;

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LinkedList<String> errorMessages() {
        LinkedList<String> ret = new LinkedList<String>();
        if(text == null)
            ret.push("no text specified");
        if(parameter == null)
            ret.push("no parameter specified");
        return ret;
    }
}
