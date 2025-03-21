package fr.univcotedazur.teamj.kiwicard.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:persistence.properties")
public class PersistenceJpaConfig {
}
