FROM openjdk:8-jre

RUN addgroup --system pti2017 && adduser --system --home /home/pti2017 --group pti2017
COPY vertx-presentation-1.0-SNAPSHOT-fat.jar /home/pti2017

ENV JAVA_OPTS -Xmx512m

RUN chown -R pti2017:pti2017 /home/pti2017
WORKDIR /home/pti2017

USER pti2017

# Expose port and run
EXPOSE 8080
CMD ["java", "-jar", "vertx-presentation-1.0-SNAPSHOT-fat.jar"]
