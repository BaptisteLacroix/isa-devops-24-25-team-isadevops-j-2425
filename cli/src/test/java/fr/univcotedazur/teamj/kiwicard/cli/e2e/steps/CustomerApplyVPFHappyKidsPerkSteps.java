package fr.univcotedazur.teamj.kiwicard.cli.e2e.steps;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.commands.CustomerCommands;
import fr.univcotedazur.teamj.kiwicard.cli.commands.PartnerCommands;
import fr.univcotedazur.teamj.kiwicard.cli.commands.PerksCommands;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCustomer;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CustomerApplyVPFHappyKidsPerkSteps {

    @Autowired
    private CustomerCommands customerCommands;

    @Autowired
    private PartnerCommands partnerCommands;

    @Autowired
    private PerksCommands perksCommands;

    @Autowired
    private CliSession cliSession;

    @Autowired
    private DataSource dataSource;

    private String response;

    private CliCustomer cliCustomer;

    @BeforeStep
    public void resetResponse() {
        response = null;
    }

    @Given("[Pierre HappyKids] a simple dataset")
    public void aSimpleDataset() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("data/import.sql"));
    }

    @Given("le client Pierre Cailloux est connecté en tant que client VFP avec son email {string}")
    public void le_client_pierre_cailloux_est_connecte_en_tant_que_client_vfp_avec_son_email(String email) {
        cliCustomer = new CliCustomer(
                email,
                "Pierre",
                "Cailloux",
                "6 rue des orthopésistes parce que Pierre nous fait mal aux Pieds avec ses cailloux, Gallet",
                true,
                null,
                "1234567896"
        );
        cliSession.logIn(email);
    }

    @When("Pierre recherche le partner {string}")
    public void pierre_recherche_le_partner(String arg0) {
        response = partnerCommands.partners();
        assertTrue(response.contains(arg0));
    }


    @Then("Pierre consulte les avantages du partenaire {string} \\(HappyKids)")
    public void pierre_consulte_les_avantages_du_partenaire_happy_kids(String arg0) {
        try {
            partnerCommands.consultPartnerPerks(arg0);
        } catch (Exception e) {
            response = e.getMessage();
        }
        assertNull(response);
    }

    @Then("Il décide de réserver un créneau horaire grâce à l'item {string} à partir de maintenant à {string}h pour une durée de {string} heures")
    public void il_decide_de_reserver_un_creneau_horaire_grace_a_l_item_a_partir_de_maintenant_a_h_pour_une_duree_de_heures(String arg0, String arg1, String arg2) {
        try {
            partnerCommands.reserveTimeSlot(
                    cliCustomer.email(),
                    Long.parseLong(arg0),
                    LocalDateTime.now().withHour(Integer.parseInt(arg1)),
                    Integer.parseInt(arg2)
            );
        } catch (RuntimeException e) {
            response = e.getMessage();
        }
        assertNull(response);
    }

    @Then("Il décide d appliquer l avantage {string} VFP et recoit bien le message suivant {string}")
    public void il_decide_d_appliquer_l_avantage_vfp_et_recoit_bien_le_message_suivant(String arg0, String arg1) {
        response = perksCommands.applyPerk(Integer.parseInt(arg0), cliCustomer.email());
        assertTrue(response.contains(arg1));
    }

    @Then("Il observe que son panier est mis à jour avec le perks de VFP")
    public void il_observe_que_son_panier_est_mis_a_jour_avec_le_perks_de_vfp() {
        response = customerCommands.getCart(cliCustomer.email());
        assertTrue(response.contains("HappyKids"));
    }

    @Then("Il décide de payer le panier et recoit un message de validation {string}")
    public void il_decide_de_payer_le_panier_et_recoit_un_message_de_validation(String arg0) {
        response = customerCommands.payCart(cliCustomer.email());
        assertTrue(response.contains(arg0));
    }

    @Then("Le panier est bien vidé et recoit le message d erreur suivant {string}")
    public void le_panier_est_bien_vide_et_recoit_le_message_d_erreur_suivant(String arg0) {
        try {
            response = customerCommands.getCart(cliCustomer.email());
        } catch (RuntimeException e) {
            response = e.getMessage();
        }
        assertTrue(response.contains(arg0));
    }
}
