package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.model.CliItem;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPartner;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPerk;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
public class PartnerCommands {

    public static final String BASE_URI = "/partners";

    private final WebClient webClient;

    @Autowired
    public PartnerCommands(WebClient webClient) {
        this.webClient = webClient;
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

    @ShellMethod("Show items of a partner (partneritems PARTNER_ID)")
    public String partneritems(long partnerId) {
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
                .uri("partners/" + partnerId + "/perks")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(new ParameterizedTypeReference<List<CliPerk>>() {})
                .block();
        printPerks(perksList);
    }

    private void printPerks(List<CliPerk> perks) {
        if (perks.isEmpty()) {
            System.out.println("No perks available for this partner.");
            return;
        }
        System.out.println("List of Perks:\n");
        for (CliPerk perk : perks) {
            System.out.println("Perk ID: " + perk.perkId() + "\nDescription: " + perk.description() + "\n");
        }
    }
}
