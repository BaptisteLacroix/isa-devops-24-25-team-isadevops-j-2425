package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPartner;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.KiwiCardQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@ShellComponent
public class SessionService {

    private final WebClient webClient;
    private final CliSession session;

    @Autowired
    public SessionService(WebClient webClient, CliSession session) {
        this.webClient = webClient;
        this.session = session;
    }

    /**
     * Commande CLI pour se connecter en tant que client ou partenaire.
     * Exemple d'utilisation :
     * login -c
     *
     * @param customerEmail L'email du client
     * @param partnerId     L'identifiant du partenaire
     * @return Message de confirmation ou message d'erreur
     */
    @ShellMethod(value = """
            Log in as a customer or a partner:
            Usage: login -c <customer-email> | -p <partner-id>
            Parameters:
                -c, --customer The email address of the customer.
                -p, --partner  The ID of the partner.
            Example:
                login -c""")
    public String login(@ShellOption(value = {"-c", "--customer"}, defaultValue = "") String customerEmail,
                        @ShellOption(value = {"-p", "--partner"}, defaultValue = "-1") Long partnerId) {
        if (!Objects.equals(customerEmail, "") && partnerId == -1) {
            loginAsCustomer(customerEmail);
            return "Connecté en tant que client " + customerEmail;
        } else if (partnerId != -1 && Objects.equals(customerEmail, "")) {
            String partnerName = loginPartner(partnerId);
            return "Connecté en tant que partenaire " + partnerName;
        } else {
            return "Veuillez spécifier soit un client avec un email (-c <email>) ou bien un partenaire avec son identifiant (-p <partnerId>)";
        }
    }


    private void loginAsCustomer(String customerEmail) {
        if (!customerEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
        webClient.get()
                .uri("/customers?email=" + customerEmail)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> {
                            throw new KiwiCardQueryException(error.errorMessage());
                        }))
                .bodyToMono(String.class)
                .block();
        session.logIn(customerEmail);
    }

    private String loginPartner(long partnerId) {
        String partnerName = webClient.get()
                .uri("/partners/" + partnerId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new KiwiCardQueryException(error.errorMessage()))))
                .bodyToMono(CliPartner.class)
                .map(CliPartner::name)
                .block();
        session.logIn(partnerId);
        return partnerName;
    }
}
