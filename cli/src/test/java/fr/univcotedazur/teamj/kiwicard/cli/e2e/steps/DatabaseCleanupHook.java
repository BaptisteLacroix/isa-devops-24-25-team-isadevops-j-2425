package fr.univcotedazur.teamj.kiwicard.cli.e2e.steps;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class DatabaseCleanupHook {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before(order = 0)
    public void cleanDatabase() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT tablename FROM pg_tables WHERE schemaname = 'public'",
                String.class
        );
        if (!tables.isEmpty()) {
            String tableNames = String.join(", ", tables);
            jdbcTemplate.execute("TRUNCATE TABLE " + tableNames + " CASCADE");
        }
    }
}

