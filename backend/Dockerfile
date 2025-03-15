# Multi-stage build
FROM eclipse-temurin:21-jdk-noble AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline
COPY ./src ./src
RUN ./mvnw -B -DskipTests=true verify

FROM eclipse-temurin:21-jre-noble
ARG JAR_FILE
RUN addgroup dockergroup; adduser --ingroup dockergroup --disabled-password --system --shell /bin/false dockeruser
WORKDIR /opt/app
COPY --from=builder /opt/app/target/${JAR_FILE} ./app.jar
RUN chown -R dockeruser:dockergroup /opt/app
EXPOSE 8080
# Starting the service (shell form of ENTRYPOINT used for substitution)
USER dockeruser
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar", "--bank.host.baseurl=${BANK_WITH_PORT}", "--cardeditor.host.baseurl=${CARD_EDITOR_WITH_PORT}", "--happykids.host.baseurl=${HAPPY_KIDS_WITH_PORT}"]
