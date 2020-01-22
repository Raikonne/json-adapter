FROM adoptopenjdk/openjdk11:alpine

COPY target/json-adapter-1.0.jar /

EXPOSE 8180

CMD java -jar json-adapter-1.0.jar
