package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.model.CliCustomerSubscribe;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPurchase;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static fr.univcotedazur.teamj.kiwicard.cli.constants.Constants.LOGGED_IN_ID_PLACEHOLDER;

@ShellComponent
public class CustomerCommands {

    public static final String BASE_URI = "/customers";
    public static final String BASE_CART_URI = "/cart";
    private final WebClient webClient;

    private final CliSession cliSession;

    @Autowired
    public CustomerCommands(WebClient webClient, CliSession cliSession) {
        this.webClient = webClient;
        this.cliSession = cliSession;
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
        CliCustomerSubscribe registrationDTO = new CliCustomerSubscribe(email, firstname, surname, address);

        // Appel vers le CustomerController pour enregistrer le client
        webClient.post()
                .uri(BASE_URI)
                .bodyValue(registrationDTO)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .toBodilessEntity()
                .block();
        cliSession.logIn(email);
        return "Register client successfuly, you are now logged in as " + email;
    }

    /**
     * CLI command to pay a customer's cart.
     * Example usage:
     * pay-cart --customer-email pierre.dupont@email.com
     *
     * @param customerEmail The email of the customer whose cart should be paid. If unspecified, uses the logged-in customer.
     * @return Confirmation message with purchase details or error message
     */
    @ShellMethod(value="Pay cart", key="pay-cart")
    public String payCart(@ShellOption(defaultValue = LOGGED_IN_ID_PLACEHOLDER) String customerEmail) {
        customerEmail = cliSession.tryInjectingCustomerEmail(customerEmail);
        if (customerEmail == null) return "Invalid customer email";
        System.out.println("Validation et paiment du panier du client " + customerEmail + " : ");

        return webClient.post()
                .uri( BASE_CART_URI + "/" + customerEmail + "/validate")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(CliPurchase.class)
                .map(res -> "Cart was purchased successfully, purchase details : \n" + res.toString().replaceAll("(?m)^", "\t"))
                .block();
    }
}
