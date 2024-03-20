## Deprecation

This library is not maintained since years, so it is now archived. I recommend [Spacy](https://spacy.io/) for NLP tasks and [Wiktextract](https://github.com/tatuylonen/wiktextract) for a lexical database (for Italian or pretty much every language).

[![Build Status](https://travis-ci.org/jacopofar/italian-nlp-library.svg?branch=master)](https://travis-ci.org/jacopofar/italian-nlp-library)

Italian NLP library
===================

A Java 8 library or REST server to perform NLP tasks on Italian language, more specifically is able to:

* detect the conjugation (person, number, time and mode) of a givern verb
* conjugate verbs
* detect stopwords
* detect numbers
* PoS tagging, sentencing and tokening (based on OpenNLP)

Verb detection and conjugation are based on an analysis of en.wiktionary, containing about 9000 verb lemmas. When a root is not found, suffixed are used instead.

Use as a REST server
====================
The easiest way is to lunch it with Docker:

    docker run -p 5678:5678jacopofar/italian-nlp-library

__POS tagger__

    curl -X POST -H "Content-Type: application/json"  -d '{"text":"Mi piace correre e scherzare ma anche bere una tazza di tè"}' "http://localhost:5678/postagger"
    
    {
    "annotations": [
      {
        "span_start": 0,
       "span_end": 2,
       "annotation": {
        "POS": "PC"
      }
    },
    {
      "span_start": 3,
      ...
    
__verb conjugations__

    curl "http://localhost:5678/conjugations/mangiare"

    {
    "indicative past historic 2s": "mangiasti",
    "indicative future 1s": "mangerò",
    "indicative future 1p": "mangeremo",
    ...
__match POS tags__

    curl -X POST -H "Content-Type: application/json"  -d '{"parameter":"S.+","text":"Mi piace correre e scherzare ma anche bere una tazza di tè"}' "http://localhost:5678/posmatch"

Use as a library
================
Use Maven to build and install it, `mvn package` to build a JAR
To use and test the library is necessary to have a set of resource files which can be downloaded from the [releases page](https://github.com/jacopofar/italian-nlp-library/releases)
