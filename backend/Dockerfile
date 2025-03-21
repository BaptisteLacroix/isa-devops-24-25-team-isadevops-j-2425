FROM eclipse-temurin:21-jre-noble
RUN addgroup dockergroup && adduser --ingroup dockergroup --disabled-password --system --shell /bin/false dockeruser

WORKDIR /opt/app
COPY app.jar ./app.jar
RUN chown -R dockeruser:dockergroup /opt/app

EXPOSE 8080
USER dockeruser
ENTRYPOINT ["java", "-jar", "/opt/app/app.jar", "--bank.host.baseurl=${BANK_WITH_PORT}", "--cardeditor.host.baseurl=${CARD_EDITOR_WITH_PORT}", "--happykids.host.baseurl=${HAPPY_KIDS_WITH_PORT}"]
