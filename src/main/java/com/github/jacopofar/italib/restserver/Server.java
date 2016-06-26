package com.github.jacopofar.italib.restserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jacopofar.italib.ItalianModel;
import com.github.jacopofar.italib.postagger.POSUtils;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

import static spark.Spark.*;

/**
 * Expose the italian NLP library functionalities as REST services
 */
public class Server {
    private static ItalianModel im = null;

    public static void main(String[] args) throws IOException {

        System.out.println("Loading model...");
        try {
            im = new ItalianModel();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (SQLException e) {
            System.exit(2);
            e.printStackTrace();
        }

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        port(5678);

        //show exceptions in console and HTTP responses
        exception(Exception.class, (exception, request, response) -> {
            //show the exceptions using stdout
            System.out.println("Exception:");
            exception.printStackTrace(System.out);
            response.status(400);
            response.body(exception.getMessage());
        });

        post("/postagger", (request, response) -> {
            ObjectMapper mapper = new ObjectMapper();
            AnnotationRequest ar = mapper.readValue(request.body(), AnnotationRequest.class);
            if(ar.errorMessages().size() != 0){
                response.status(400);
                return "invalid request body. Errors: " + ar.errorMessages() ;
            }
            String tagRegex = ar.getParameter();
            HashSet<String> acceptedTags = new HashSet<>();
            for(String tag:POSUtils.getPossibleTags()){
                if(tag.matches(tagRegex))
                    acceptedTags.add(tag);
            }

            if(acceptedTags.isEmpty())
                throw new RuntimeException("tag regex "+tagRegex+" not recognized, possible tags: " + Arrays.toString(POSUtils.getPossibleTags()));
            Span[] spans = im.getPosTags(ar.getText());
            //TODO add an helper to create a list on Annotations and send them directly
            JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
            ObjectNode retVal = nodeFactory.objectNode();
            ArrayNode annotationArray = retVal.putArray("annotations");
            for(Span s:spans){
                if(acceptedTags.contains(s.getType())){
                    ObjectNode annotation = nodeFactory.objectNode();
                    annotation.put("span_start", s.getStart());
                    annotation.put("span_end", s.getEnd());
                    annotation.put("type",s.getType());
                    annotationArray.add(annotation);
                }
            }

            response.type("application/json");
            return retVal.toString();
        });
    }
}