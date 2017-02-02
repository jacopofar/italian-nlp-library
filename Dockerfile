FROM java:8-jdk
RUN apt-get update && apt-get install -y maven
RUN update-java-alternatives -s java-1.8.0-openjdk-amd64
ADD . /opt/italian-nlp-library
RUN wget https://github.com/jacopofar/italian-nlp-library/releases/download/v0.1/it_verb_model.db
RUN ls && mkdir -p /opt/italian-nlp-library/target/classes/ && mkdir -p /opt/italian-nlp-library/target/test-classes/ && cp -v it_verb_model.db /opt/italian-nlp-library/target/classes/ && cp -v it_verb_model.db /opt/italian-nlp-library/target/test-classes/
RUN wget https://github.com/jacopofar/italian-nlp-library/releases/download/v0.1/it_POS_model.db
RUN cp -v it_POS_model.db /opt/italian-nlp-library/target/classes/ && cp -v it_POS_model.db /opt/italian-nlp-library/target/test-classes/
RUN wget https://github.com/jacopofar/italian-nlp-library/releases/download/v0.1/it-token.bin
RUN ls && mkdir -p target/classes && cp -v it-token.bin /opt/italian-nlp-library/target/classes/ && cp -v it-token.bin /opt/italian-nlp-library/target/test-classes/
RUN wget https://github.com/jacopofar/italian-nlp-library/releases/download/v0.1/it-sent.bin
RUN cp -v it-sent.bin /opt/italian-nlp-library/target/classes/ && cp -v it-sent.bin /opt/italian-nlp-library/target/test-classes/
RUN wget https://github.com/jacopofar/italian-nlp-library/releases/download/v0.1/it-pos-maxent.bin
RUN cp -v it-pos-maxent.bin /opt/italian-nlp-library/target/classes/ && cp -v it-pos-maxent.bin /opt/italian-nlp-library/target/test-classes/
RUN wget https://github.com/jacopofar/italian-nlp-library/releases/download/v0.1/stopwords.txt
RUN cp -v stopwords.txt /opt/italian-nlp-library/target/classes/ && cp -v stopwords.txt /opt/italian-nlp-library/target/test-classes/
RUN cd /opt/italian-nlp-library && mvn install
WORKDIR /opt/italian-nlp-library
CMD ["mvn","exec:java"]
