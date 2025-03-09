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

import java.util.List;
import java.util.stream.Collectors;

import static fr.univcotedazur.teamj.kiwicard.cli.constants.Constants.LOGGED_IN_ID_PLACEHOLDER;

@ShellComponent
public class PartnerCommands {

    public static final String BASE_URI = "/partners";

    private final WebClient webClient;
    private final CliSession cliSession;

    @Autowired
    public PartnerCommands(WebClient webClient, CliSession cliSession) {
        this.webClient = webClient;
        this.cliSession = cliSession;
    }

    @ShellMethod("Show all partners (partners)")
    public String partners() {
        return webClient.get()
                .uri(BASE_URI)
                .retrieve()
                .bodyToFlux(CliPartner.class)
                .map(CliPartner::toString)
                .collect(Collectors.joining("\n"))
                .block();
    }

    @ShellMethod(value = "Show items of a partner",key = "partner-items")
    public String partnerItems(@ShellOption(defaultValue = LOGGED_IN_ID_PLACEHOLDER) String partnerId) {
        partnerId = cliSession.tryInjectingPartnerId(partnerId);
        if (partnerId == null) return "Invalid partner id";
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
     * Commande CLI pour consulter les avantages d'un partenaire.
     * Exemple d'utilisation :
     * consult-partner-perks --partnerId <partnerId>
     *
     * @param partnerId L'identifiant du partenaire pour lequel consulter les avantages
     */
    @ShellMethod("""
            
                Consult the perks of a partner:
                Usage: consult-partner-perks --partnerId <partnerId>
            
                Parameters:
                    --partnerId  The ID of the partner whose perks you want to consult.
            
                Example:
                    consult-partner-perks --partnerId "12345"
            """)
    public void consultPartnerPerks(String partnerId) {
        List<CliPerk> perksList = webClient.get()
                .uri(BASE_URI + "/" + partnerId + "/perks")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToFlux(CliPerk.class)
                .collectList()
                .block();
        if (perksList == null) {
            System.out.println("No perks available for this partner.");
            return;
        }
        printPerks(perksList);
    }

    private void printPerks(List<CliPerk> perks) {
        if (perks.isEmpty()) {
            System.out.println("No perks available for this partner.");
            return;
        }
        System.out.println("List of Perks:\n");
        perks.forEach(System.out::println);
    }
}
