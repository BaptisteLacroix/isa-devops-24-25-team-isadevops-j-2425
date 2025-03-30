package fr.univcotedazur.teamj.kiwicard.cli.e2e.steps;

import fr.univcotedazur.teamj.kiwicard.cli.commands.MonitoringCommands;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PartnerConsultsItsHistorySteps {


    @Autowired
    private MonitoringCommands monitoringCommands;

    private final Map<String, String> historyOutputMap = new HashMap<>();

    @When("the partner {string} consults his purchase history")
    public void consultHistory(String email) {
        historyOutputMap.put(email, monitoringCommands.partnerHistory(email, ""));
    }

    @Then("the partner {string} sees the following history")
    public void seesPurchases(String email, String history) {
        // Prepare the expected and actual history strings
        String expectedHistory = history.replace("\\t", "\t")
                .lines().
                map(String::trim).
                reduce("", (acc, line) -> acc + line + "\n");
        String actualHistory = historyOutputMap.get(email);
        actualHistory = actualHistory.replaceAll("\\d{2}/\\d{2}/\\d{4} à \\d{2}:\\d{2}", "DATE_PLACEHOLDER")
                .replaceAll("\\d{2}:\\d{2}", "TIME_PLACEHOLDER")
                .replaceAll("\\n$", "")
                .lines().
                map(String::trim).
                reduce("", (acc, line) -> acc + line + "\n");
        assertEquals(expectedHistory, actualHistory);
    }
}
