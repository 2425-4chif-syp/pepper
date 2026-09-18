FROM eclipse-temurin:25-jre

RUN mkdir -p /opt/application
RUN ls -lah /
COPY ./target/*-runner.jar /opt/application/backend.jar
WORKDIR /opt/application
CMD [ "java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "backend.jar" ]
