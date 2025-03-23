package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliApplyPerk;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPerk;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static fr.univcotedazur.teamj.kiwicard.cli.constants.Constants.LOGGED_IN_ID_PLACEHOLDER;

@ShellComponent
public class PerksCommands {

    public static final String BASE_URI = "/perks";

    private final WebClient webClient;

    private final CliSession cliSession;

    @Autowired
    public PerksCommands(WebClient webClient, CliSession cliSession) {
        this.webClient = webClient;
        this.cliSession = cliSession;
    }

    /**
     * Commande CLI pour consulter tous les avantages applicables au panier d'un client.
     * Exemple d'utilisation :
     * list-perks --customerEmail pierre.dupont@email.com
     *
     * @param customerEmail L'email du client
     * @return Les avantages applicables au panier du client
     */
    @ShellMethod("""
            
                List all perks applicable to a customer's cart:
                Usage: list-perks --customer-email <customer-email>
            
                Parameters:
                    --customer-email/-e The email address of the customer.
            
                Example:
                    list-perks --customer-email pierre.dupont@email.com
            """)
    public String listPerks(@ShellOption(value = {"-e", "--customer-email"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER) String customerEmail) {
        customerEmail = cliSession.tryInjectingCustomerEmail(customerEmail);
        if (customerEmail == null) return "Erreur : email client invalide";
        return webClient.get()
                .uri(BASE_URI + "/consumable?consumerEmail=" + customerEmail)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage())))
                )
                .bodyToFlux(CliPerk.class)
                .map(CliPerk::toString)
                .collect(Collectors.joining("\n"))
                .map(s->s.isEmpty() ? "Aucun avantage applicable pour votre panier actuel, consulter les avantages proposer par le commernçant avec `consult-partner-perks --partnerId <id>`" : s)
                .block();
    }

    /**
     * Command to apply a perk to a customer using their email.
     * Example usage:
     * apply-perk --perkId <perkId> --customer-email <email>
     *
     * @param perkId The ID of the perk to apply.
     * @param customerEmail The email address of the customer to whom the perk is applied.
     */
    @ShellMethod("""
            
                Apply a perk to a customer:
                Usage: apply-perk --perkId <perkId> --emailCustomer <email>

                Parameters:
                    --perk-id/-p      The ID of the perk to apply.
                    --customer-email/-e  The email address of the customer to apply the perk to.

                Example:
                    apply-perk --perk-id 12345 --customer-email "customer@example.com"
            """)
    public String applyPerk(@ShellOption(value = {"--perk-id", "-p"}) long perkId,
                            @ShellOption(value = {"-e", "--customer-email"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER)  String customerEmail) {
        customerEmail = cliSession.tryInjectingCustomerEmail(customerEmail);
        if (customerEmail == null) return "Erreur : Veuillez vous connecter ou spécifier un email de client valide.";
        CliApplyPerk payload = new CliApplyPerk(customerEmail);
        String finalCustomerEmail = customerEmail;
        return webClient.post()
                .uri("/perks/" + perkId + "/apply")
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(String.class)
                .map(response -> "Ajout de l'avantage ayant l'ID : " + perkId + " au client " + finalCustomerEmail)
                .block();
    }
}
