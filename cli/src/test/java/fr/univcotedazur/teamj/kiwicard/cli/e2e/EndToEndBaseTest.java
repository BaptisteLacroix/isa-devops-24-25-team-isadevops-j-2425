package fr.univcotedazur.teamj.kiwicard.cli.e2e;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.File;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("e2e")
public abstract class EndToEndBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(EndToEndBaseTest.class);

    private static final ComposeContainer environment = new ComposeContainer(new File("./src/test/resources/docker-compose.yml"))
            .withExposedService("postgres", 5432)
            .withExposedService("wiremock-bank", 8080)
            .withExposedService("wiremock-card", 8080)
            .withExposedService("wiremock-happykids", 8080)
            .withExposedService("kiwicard-server", 8080)
            .withLogConsumer("postgres", new Slf4jLogConsumer(LoggerFactory.getLogger("postgres")))
            .withLogConsumer("wiremock-bank", new Slf4jLogConsumer(LoggerFactory.getLogger("wiremock-bank")))
            .withLogConsumer("wiremock-card", new Slf4jLogConsumer(LoggerFactory.getLogger("wiremock-card")))
            .withLogConsumer("wiremock-happykids", new Slf4jLogConsumer(LoggerFactory.getLogger("wiremock-happykids")))
            .withLogConsumer("kiwicard-server", new Slf4jLogConsumer(LoggerFactory.getLogger("kiwicard-server")));

    static void startContainers() {
        logger.info("Starting containers");
        environment.start();
        System.setProperty("DATABASE_URL", getDatabaseUrl());
        System.setProperty("WIREMOCK_BANK_URL", getWiremockBankUrl());
        System.setProperty("CARD_WITH_PORT", getWiremockCardUrl());
        System.setProperty("cardeditor.host.baseurl", getWiremockCardUrl());
        System.setProperty("WIREMOCK_HAPPYKIDS_URL", getWiremockHappyKidsUrl());
        System.setProperty("BACKEND_URL", getBackendUrl());
        logger.info("Containers started");
    }

    @AfterAll
    static void stopContainers() {
        environment.stop();
    }

    private static String getDatabaseUrl() {
        return "jdbc:postgresql://localhost:" + getMappedPort("postgres", 5432) + "/kiwicard-db";
    }

    private static String getWiremockBankUrl() {
        return "http://localhost:" + getMappedPort("wiremock-bank", 8080);
    }

    private static String getWiremockCardUrl() {
        return "http://localhost:" + getMappedPort("wiremock-card", 8080);
    }

    private static String getWiremockHappyKidsUrl() {
        return "http://localhost:" + getMappedPort("wiremock-happykids", 8080);
    }

    private static String getBackendUrl() {
        return "http://localhost:" + getMappedPort("kiwicard-server", 8080);
    }

    static int getMappedPort(String serviceName, int originalPort) {
        ContainerState service = environment.getContainerByServiceName(serviceName).orElseThrow();
        return service.getMappedPort(originalPort);
    }
}
