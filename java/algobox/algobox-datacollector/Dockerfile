FROM eu.gcr.io/dainco-algobox/algobox-base-java

COPY build/libs/algobox-datacollector.jar /srv/

ENV CONSUL_CLIENT_HOST=127.0.0.1

ENTRYPOINT ["java", "-jar", "/srv/algobox-datacollector.jar"]

EXPOSE 8080
