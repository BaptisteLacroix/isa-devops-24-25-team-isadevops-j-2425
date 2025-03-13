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
                Usage: list-perks --customerEmail <customer-email>
            
                Parameters:
                    --customerEmail The email address of the customer.
            
                Example:
                    list-perks --customerEmail pierre.dupont@email.com
            """)
    public String listPerks(String customerEmail) {
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
                .block();
    }

    /**
     * Command to apply a perk to a customer using their email.
     * Example usage:
     * apply-perk --perkId <perkId> --emailCustomer <email>
     *
     * @param perkId The ID of the perk to apply.
     * @param emailCustomer The email address of the customer to whom the perk is applied.
     */
    @ShellMethod("""
            
                Apply a perk to a customer:
                Usage: apply-perk --perkId <perkId> --emailCustomer <email>

                Parameters:
                    --perkId      The ID of the perk to apply.
                    --emailCustomer  The email address of the customer to apply the perk to.

                Example:
                    apply-perk --perkId 12345 --emailCustomer "customer@example.com"
            """)
    public String applyPerk(@ShellOption long perkId, @ShellOption String emailCustomer) {
        CliApplyPerk payload = new CliApplyPerk(emailCustomer);

        return webClient.post()
                .uri("/perks/" + perkId + "/apply")
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(String.class)
                .map(response -> "Ajout de l'avantage ayant l'ID : " + perkId + " au client " + emailCustomer)
                .block();
    }
}
