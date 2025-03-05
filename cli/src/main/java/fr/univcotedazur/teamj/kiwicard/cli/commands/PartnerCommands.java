package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.model.CliItem;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPartner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;

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
                .bodyToFlux(CliItem.class)
                .map(CliItem::toString)
                .collect(Collectors.joining("\n"))
                .block();
    }

}
