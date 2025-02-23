package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.model.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.cli.model.CustomerRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@ShellComponent
public class CustomerCommands {

    public static final String BASE_URI = "/customer";

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
        CustomerRegistrationDTO registrationDTO = new CustomerRegistrationDTO(surname, firstname, email, address);

        try {
            // Appel vers le CustomerController pour enregistrer le client
            CustomerDTO registeredCustomer = webClient.post()
                    .uri(BASE_URI + "/register")
                    .bodyValue(registrationDTO)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.CONFLICT) {
                            return Mono.error(new RuntimeException("Email already used. Please try another one."));
                        }
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Registration failed: " + errorBody)));
                    })
                    .bodyToMono(CustomerDTO.class)
                    .block();

            return "Client registered successfully: " + registeredCustomer;
        } catch (WebClientResponseException e) {
            // Cas d'erreur spécifique pour les problèmes de réponse HTTP
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                return "Error: The email address is already used. Please try a different one.";
            }
            if (e.getStatusCode().is5xxServerError()) {
                return "Error: A server error occurred. Please try again later.";
            }
            return "Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (RuntimeException e) {
            // Cas d'erreur générique
            return "An error occurred during client registration: " + e.getMessage() + ". Please check the provided details.";
        }
    }
}
