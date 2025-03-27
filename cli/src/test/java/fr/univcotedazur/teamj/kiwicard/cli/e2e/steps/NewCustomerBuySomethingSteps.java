package fr.univcotedazur.teamj.kiwicard.cli.e2e.steps;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.commands.CustomerCommands;
import fr.univcotedazur.teamj.kiwicard.cli.commands.PerksCommands;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewCustomerBuySomethingSteps {

    @Autowired
    private CustomerCommands customerCommands;

    @Autowired
    private PerksCommands perksCommands;

    @Autowired
    private CliSession cliSession;

    @Autowired
    private DataSource dataSource;

    private String response;

    @Given("a simple dataset")
    public void aSimpleDataset() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("data/import.sql"));
    }

    @Given("the client {string} is registered with surname {string}, firstname {string} and address {string}")
    public void registerClient(String email, String surname, String firstname, String address) {
        response = customerCommands.registerClient(surname, firstname, email, address);
        // Vérifie que la réponse contient l'email pour confirmer l'inscription
        assertTrue(response.contains(email), "L'inscription a échoué");
    }

    @When("the client adds item with id {string} and quantity {int} to the cart")
    public void addItemToCart(String itemId, int quantity) {
        // Utilise le client connecté (par exemple, via cliSession.getCurrentUser())
        String currentUser = cliSession.getLoggedInCustomerEmail();
        customerCommands.addItemToCart(currentUser, Long.parseLong(itemId), quantity);
    }

    @When("the client applies perk with id {string}")
    public void applyPerk(String perkId) {
        response = perksCommands.applyPerk(Long.parseLong(perkId), cliSession.getLoggedInCustomerEmail());
        assertTrue(response.contains("Ajout de l'avantage ayant l'ID"), "L'application du perk a échoué");
    }

    @When("the client pays the cart")
    public void payCart() {
        response = customerCommands.payCart(cliSession.getLoggedInCustomerEmail());
    }

    @Then("the purchase is successful")
    public void verifyPurchase() {
        assertTrue(response.contains("Le panier a été validé avec succès"), "L'achat n'a pas abouti");
    }
}

