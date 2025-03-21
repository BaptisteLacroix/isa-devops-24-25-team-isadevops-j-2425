package fr.univcotedazur.teamj.kiwicard.cli.e2e;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = {"fr.univcotedazur.teamj.kiwicard.cli"})
public class CucumberTestContextConfig {

}
