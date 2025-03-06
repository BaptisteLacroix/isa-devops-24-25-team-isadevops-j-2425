package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.model.CliCustomerSubscribe;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ShellComponent
public class CustomerCommands {

    public static final String BASE_URI = "/customers";

    private final WebClient webClient;

    @Autowired
    public CustomerCommands(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Commande CLI pour enregistrer un client.
     * Exemple d'utilisation :
     * register-client --surname Pierre --firstname Dupont --email pierre.dupont@email.com
     * --address "123 rue de Paris"
     *
     * @param surname   Le nom de famille du client
     * @param firstname Le prénom du client
     * @param email     L'email du client
     * @param address   L'adresse du client
     * @return Message de confirmation ou message d'erreur
     */
    @ShellMethod("""
            
                Register a new client:
                Usage: register-client --surname <surname> --firstname <firstname> --email <email> --address <address>
            
                Parameters:
                    --surname   The surname of the client.
                    --firstname The first name of the client.
                    --email     The email address of the client.
                    --address   The address of the client.
            
                Example:
                    register-client --surname "Doe" --firstname "John" --email "john.doe@example.com" --address "123 Main St, City, Country"
            """)
    public String registerClient(String surname, String firstname, String email, String address) {
        // Création du DTO d'inscription
        CliCustomerSubscribe registrationDTO = new CliCustomerSubscribe(surname, firstname, email, address);

        // Appel vers le CustomerController pour enregistrer le client
        webClient.post()
                .uri(BASE_URI)
                .bodyValue(registrationDTO)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .toBodilessEntity()
                .block();

        return "User registered successfully";
    }
}
