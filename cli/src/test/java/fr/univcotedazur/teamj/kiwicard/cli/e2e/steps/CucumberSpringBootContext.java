package fr.univcotedazur.teamj.kiwicard.cli.e2e.steps;


import fr.univcotedazur.teamj.kiwicard.cli.CliApplication;
import fr.univcotedazur.teamj.kiwicard.cli.e2e.CucumberTestContextConfig;
import fr.univcotedazur.teamj.kiwicard.cli.e2e.SpringBootContextInitializer;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.shell.interactive.enabled=false", "spring.shell.script.enabled=false"})
@ContextConfiguration(
        initializers = {SpringBootContextInitializer.class},
        classes = {CliApplication.class, CucumberTestContextConfig.class}
)
@ActiveProfiles(profiles = {"e2e"})
public class CucumberSpringBootContext {
}
