package fr.univcotedazur.teamj.kiwicard.cli.e2e;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

@TestConfiguration
@ComponentScan(basePackages = {"fr.univcotedazur.teamj.kiwicard.cli"})
public class CucumberTestContextConfig {
    @Bean
    public DataSource dataSource(@Value("${spring.datasource.username}") String username,
                                 @Value("${spring.datasource.password}") String password) {
        return DataSourceBuilder.create()
                .url(System.getProperty("DATABASE_URL"))
                .username(username)
                .password(password)
                .build();
    }
}
