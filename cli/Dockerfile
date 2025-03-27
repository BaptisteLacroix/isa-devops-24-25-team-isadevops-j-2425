
FROM eclipse-temurin:21-jre-noble
RUN addgroup dockergroup && adduser --ingroup dockergroup --disabled-password --system --shell /bin/false dockeruser

WORKDIR /opt/app
COPY app.jar ./app.jar
RUN chown -R dockeruser:dockergroup /opt/app

# Starting the service (shell form of ENTRYPOINT used for substitution)
USER dockeruser
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar", "--kiwi-card.host.baseurl=${SERVER_WITH_PORT}"]
