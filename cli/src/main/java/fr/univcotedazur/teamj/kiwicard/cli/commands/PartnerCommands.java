package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliItem;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPartner;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPerk;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static fr.univcotedazur.teamj.kiwicard.cli.constants.Constants.LOGGED_IN_ID_PLACEHOLDER;

/**
 * Provides a set of CLI commands related to partner information and customer cart management.
 * This class includes commands for listing partners, viewing items for a specific partner,
 * consulting partner perks, adding items to a customer's cart, and reserving time slots.
 * <p>
 * The commands interact with external services through a WebClient to fetch partner and cart data.
 * Each command can be executed from a shell interface and includes specific usage examples.
 * <p>
 * Example commands:
 * - `partners`: Lists all available partners.
 * - `partner-items`: Shows items associated with a specific partner.
 * - `consult-partner-perks`: Consults the perks for a specific partner.
 * - `add-item-to-cart`: Adds an item to a customer's cart.
 * - `reserve-time-slot`: Reserves a time slot for a customer's cart.
 */
@ShellComponent
public class PartnerCommands {

    public static final String BASE_URI = "/partners";

    private final WebClient webClient;
    private final CliSession cliSession;

    /**
     * Constructs a new PartnerCommands instance with the specified dependencies.
     *
     * @param webClient  The WebClient instance used to interact with the external API for partner and cart data.
     * @param cliSession The session information related to the current CLI session.
     */
    @Autowired
    public PartnerCommands(WebClient webClient, CliSession cliSession) {
        this.webClient = webClient;
        this.cliSession = cliSession;
    }

    /**
     * Lists all available partners.
     *
     * @return A string representing all partners, each displayed on a new line.
     */
    @ShellMethod(value = """
            Show all partners
            Usage: partners
            
            Example:
                partners
            """, key = "partners")
    public String partners() {
        return webClient.get()
                .uri(BASE_URI)
                .retrieve()
                .bodyToFlux(CliPartner.class)
                .map(CliPartner::toString)
                .collect(Collectors.joining("\n"))
                .block();
    }

    /**
     * Lists the items for a specific partner based on the partner's ID.
     *
     * @param partnerId The ID of the partner whose items are to be displayed.
     * @return A string containing all items associated with the partner, each displayed on a new line.
     */
    @ShellMethod(value = """
            Show items for a partner
            Usage: partner-items --partner-id <partner-id>
            
            Parameters:
                --partnerId  The ID of the partner whose items you want to display.
           
            Example:
                partner-items --partner-id 12345
            """, key = "partner-items")
    public String partnerItems(@ShellOption(value = {"-p", "--partner-id"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER) String partnerId) {
        partnerId = cliSession.tryInjectingPartnerId(partnerId);
        if (partnerId == null) return "Erreur : ID de partenaire invalide.";
        System.out.println("Récupération des items du partenaire " + partnerId + " : ");
        return webClient.get()
                .uri(BASE_URI + "/" + partnerId + "/items")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToFlux(CliItem.class)
                .map(CliItem::toString)
                .collect(Collectors.joining("\n"))
                .block();
    }

    /**
     * Consults the perks of a partner based on the partner's ID.
     * <p>
     * Example usage:
     * consult-partner-perks --partner-id <partnerId>
     *
     * @param partnerId The ID of the partner whose perks are to be consulted.
     */
    @ShellMethod("""
                Consult the perks of a partner:
                Usage: consult-partner-perks --partner-id <partner-id>
            
                Parameters:
                    --partner-id/-p  The ID of the partner whose perks you want to consult.
            
                Example:
                    consult-partner-perks --partner-id "12345"
            """)
    public String consultPartnerPerks(@ShellOption(value = {"-p", "--partner-id"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER) String partnerId) {
        partnerId = cliSession.tryInjectingPartnerId(partnerId);
        if (partnerId == null) return "Erreur : ID de partenaire invalide.";
        List<CliPerk> perksList = webClient.get()
                .uri(BASE_URI + "/" + partnerId + "/perks")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToFlux(CliPerk.class)
                .collectList()
                .block();
        if (perksList == null) {
            return "Pas d'avantages disponibles pour ce partenaire.";
        }
        return printPerks(perksList);
    }

    /**
     * Prints the perks of a partner in a formatted manner.
     *
     * @param perks A list of perks to be displayed.
     */
    private String printPerks(List<CliPerk> perks) {
        if (perks.isEmpty()) {
            return "Pas d'avantages disponibles pour ce partenaire.";
        }
        return "Liste des réductions : \n" + perks.stream()
                .map(CliPerk::toString)
                .collect(Collectors.joining("\n"));
    }
}

