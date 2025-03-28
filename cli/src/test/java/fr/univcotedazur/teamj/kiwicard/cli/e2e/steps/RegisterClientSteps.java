package fr.univcotedazur.teamj.kiwicard.cli.e2e.steps;

import fr.univcotedazur.teamj.kiwicard.cli.commands.CustomerCommands;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterClientSteps {

    @Autowired
    private CustomerCommands customerCommands;

    @Autowired
    private DataSource dataSource;

    private String response;

    @Given("[User Registration] a simple dataset")
    public void aSimpleDataset() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("data/import.sql"));
    }

    @When("I register a new customer with surname {string}, firstname {string}, email {string} and address {string}")
    public void i_have_a_new_customer(String surname, String firstname, String email, String address) {
        response = customerCommands.registerClient(surname, firstname, email, address);
        assertTrue(response.contains("Client enregistré avec succès. Vous êtes maintenant connecté en tant que : " + email));
    }

    @Then("the registration should be successful")
    public void the_registration_should_be_successful() {
        assertNotNull(response, "Registration should not be null");
    }

    @Then("I should receive a confirmation message {string}")
    public void i_should_receive_a_confirmation_message(String confirmationMessage) {
        assertTrue(response.contains(confirmationMessage), "Registration should be successful");
    }

    @Given("I have a customer with surname {string}, firstname {string}, email {string} and address {string} already registered")
    public void i_have_a_customer_already_registered(String surname, String firstname, String email, String address) {
        customerCommands.registerClient(surname, firstname, email, address);
    }

    @When("I try to register a new customer with the same surname {string}, firstname {string}, email {string} and address {string}")
    public void i_try_to_register_a_new_customer_with_same_email(String surname, String firstname, String email, String address) {
        try {
            response = customerCommands.registerClient(surname, firstname, email, address);
        } catch (RuntimeException e) {
            response = e.getMessage();
        }
    }

    @Then("I should receive an error message {string}")
    public void i_should_receive_an_error_message(String expectedMessage) {
        assertTrue(response.contains(expectedMessage), "Error message should be displayed");
    }
}
