package fr.univcotedazur.teamj.kiwicard.cli.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "json:target/reports/cucumber/cucumber.json"},
        features = "classpath:e2e",
        glue = {"fr.univcotedazur.teamj.kiwicard.cli.e2e.steps"}
)
public class CucumberTestIT {
}
