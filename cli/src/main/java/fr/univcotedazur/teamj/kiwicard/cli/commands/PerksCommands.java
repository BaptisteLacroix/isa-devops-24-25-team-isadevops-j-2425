package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPerk;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
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
     * list-perks --customer-email pierre.dupont@email.com
     *
     * @param customerEmail L'email du client
     * @return Les avantages applicables au panier du client
     */
    @ShellMethod("""
            
                List all perks applicable to a customer's cart:
                Usage: list-perks --customer-email <customer-email>
            
                Parameters:
                    --customer-email The email address of the customer.
            
                Example:
                    list-perks --customer-email pierre.dupont@email.com
            """)
    public String listPerks(String customerEmail) {
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
}
