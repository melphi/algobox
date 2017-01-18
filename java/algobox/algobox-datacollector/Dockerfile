FROM dainco/algobox-base-java

COPY build/libs/algobox-datacollector.jar /srv/

ENTRYPOINT ["java", "-jar", "/srv/algobox-datacollector.jar"]

EXPOSE 8080
