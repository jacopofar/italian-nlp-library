[![Build Status](https://travis-ci.org/jacopofar/italian-nlp-library.svg?branch=master)](https://travis-ci.org/jacopofar/italian-nlp-library)

Italian NLP library
===================

A Java 8 library to perform NLP tasks on Italian language, more specifically is able to:

* detect the conjugation (person, number, time and mode) of a givern verb
* conjugate verbs
* detect stopwords
* detect numbers
* PoS tagging, sentencing and tokening (based on OpeNLP)

Verb detection and conjugation are based on an analysis of en.wiktionary, containing about 9000 verb lemmas. When a root is not found, suffixed are used instead.

To use the library is necessary to have a set of files which can be downloaded from the [releases page](https://github.com/jacopofar/italian-nlp-library/releases)
