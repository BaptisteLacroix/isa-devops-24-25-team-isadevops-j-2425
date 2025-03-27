package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCart;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCartItemToSent;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCustomerSubscribe;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliPurchase;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static fr.univcotedazur.teamj.kiwicard.cli.constants.Constants.LOGGED_IN_ID_PLACEHOLDER;

@ShellComponent
public class CustomerCommands {

    public static final String BASE_URI = "/customers";
    public static final String BASE_CART_URI = "/cart";
    private final WebClient webClient;

    private final CliSession cliSession;
    private static final String INVALID_EMAIL_MESSAGE = "Erreur : Veuillez vous connecter ou spécifier un email de client valide.";

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
        return "Client enregistré avec succès. Vous êtes maintenant connecté en tant que : " + email;
    }


    /**
     * Adds an item to a customer's shopping cart.
     * <p>
     * Example usage:
     * add-item-to-cart --customerEmail <customerEmail> --itemId <itemId> --quantity <quantity>
     *
     * @param customerEmail The email of the customer to whom the cart belongs.
     * @param itemId        The ID of the item to be added to the cart.
     * @param quantity      The quantity of the item to be added to the cart.
     */
    @ShellMethod("""
                Add an item to a customer's cart:
                Usage: add-item-to-cart --customerEmail <customerEmail> --itemId <itemId> --quantity <quantity>
            
                Parameters:
                    --customerEmail  The email of the customer to whom the cart belongs.
                    --itemId         The ID of the item to be added to the cart.
                    --quantity       The quantity of the item to add to the cart.
            
                Example:
                    add-item-to-cart --customerEmail "customer@example.com" --itemId 123 --quantity 2
            """)
    public void addItemToCart(
            String customerEmail,
            Long itemId,
            Integer quantity
    ) {
        checkQuantity(quantity);
        CliCartItemToSent cartItemDTO = new CliCartItemToSent(quantity, null, itemId);
        CliCart updatedCart = sendCartRequest(customerEmail, cartItemDTO);

        if (updatedCart != null) {
            System.out.println("Article ajouté au panier avec succès :");
            System.out.println(updatedCart);
        } else {
            System.out.println("Erreur lors de l'ajout de l'article au panier.");
        }
    }

    /**
     * Reserves a time slot for a customer's cart.
     * <p>
     * Example usage:
     * reserve-time-slot --customerEmail <customerEmail> --startTime <startTime> --endTime <endTime> --quantity <quantity> --itemId <itemId>
     *
     * @param customerEmail The email of the customer to whom the cart belongs.
     * @param startTime     The start time for the time slot.
     * @param endTime       The end time for the time slot.
     * @param quantity      The quantity of the item to be added to the cart.
     * @param itemId        The ID of the item to be added to the cart.
     */
    @ShellMethod("""
                Reserve a time slot for a customer:
                Usage: reserve-time-slot --customerEmail <customerEmail> --startTime <startTime> --endTime <endTime> --quantity <quantity> --itemId <itemId>
            
                Parameters:
                    --customerEmail  The email of the customer to whom the cart belongs.
                    --startTime      The start time for the item in the cart.
                    --endTime        The end time for the item in the cart.
                    --quantity       The quantity of the item to add to the cart.
                    --itemId         The ID of the item to be added to the cart.
            
                Example:
                    reserve-time-slot --customerEmail "customer@example.com" --startTime "2025-03-12T10:00:00" --endTime "2025-03-12T18:00:00" --quantity 2 --itemId 123
            """)
    public void reserveTimeSlot(
            String customerEmail,
            Long itemId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer quantity
    ) {
        checkQuantity(quantity);
        CliCartItemToSent cartItemDTO = new CliCartItemToSent(quantity, startTime, itemId);
        CliCart updatedCart = sendCartRequest(customerEmail, cartItemDTO);

        if (updatedCart != null) {
            System.out.println("La réservation du créneau horaire a été effectuée avec succès:");
            System.out.println(updatedCart);
        } else {
            System.out.println("Impossible de réserver le créneau horaire.");
        }
    }

    private void checkQuantity(int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Erreur: La quantité doit être supérieur ou égale à 0.");
        }
    }

    @ShellMethod("""
                Remove an item from a customer's cart:
                Usage: remove-item-from-cart --customerEmail <customerEmail> --itemId <itemId>
                Parameters:
                    --customer-email/-e  The email of the customer whose cart will be updated.
                    --item-id/-i         The ID of the item to be removed from the cart.
                Example:
                    remove-item-from-cart --customerEmail "customer@example.com" --itemId 123
            """)
    public String removeItemFromCart(
            @ShellOption(value = {"-e", "--customer-email"}) String customerEmail,
            @ShellOption(value = {"-i", "--item-id"}) Long itemId
    ) {
        customerEmail = cliSession.tryInjectingCustomerEmail(customerEmail);
        if (customerEmail == null) {
            return INVALID_EMAIL_MESSAGE;
        }
        return webClient.delete()
                .uri(BASE_CART_URI + "/" + customerEmail + "/item/" + itemId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(CliCart.class)
                .map(cart -> "Article retiré du panier avec succès ! Détails du panier :\n" + cart.toString().replaceAll("(?m)^", "\t"))
                .block();
    }

    /**
     * Sends a request to update a customer's cart.
     *
     * @param customerEmail The email address of the customer whose cart is being updated.
     * @param cartItemDTO   The details of the cart item being added or updated.
     * @return The updated cart.
     */
    private CliCart sendCartRequest(String customerEmail, CliCartItemToSent cartItemDTO) {
        return webClient.put()
                .uri(BASE_CART_URI + "/" + customerEmail)
                .bodyValue(cartItemDTO)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(CliCart.class)
                .block();
    }


    /**
     * CLI command to pay a customer's cart.
     * Example usage:
     * pay-cart --customer-email pierre.dupont@email.com
     *
     * @param customerEmail The email of the customer whose cart should be paid. If unspecified, uses the logged-in customer.
     * @return Confirmation message with purchase details or error message
     */
    @ShellMethod(value = """
                    Pay a customer's cart:
                    Usage: pay-cart --customerEmail <customer-email>
                    Parameters:
                        --customer-email The email of the customer whose cart should be paid.
                    Example:
                        pay-cart --customerEmail clement@armeedeterre.fr"
            """, key = "pay-cart")
    public String payCart(@ShellOption(value = {"-e", "--customer-email"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER) String customerEmail) {
        customerEmail = cliSession.tryInjectingCustomerEmail(customerEmail);
        if (customerEmail == null) return INVALID_EMAIL_MESSAGE;
        return webClient.post()
                .uri(BASE_CART_URI + "/" + customerEmail + "/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(CliPurchase.class)
                .map(res -> "Le panier a été validé avec succès ! Plus de détails : " + res.toString().replaceAll("(?m)^", "\t"))
                .block();
    }

    /**
     * CLI command to retrieve a customer's cart.
     * Example usage:
     * get-cart --customer-email "john.doe@example.com"
     *
     * @param customerEmail The email of the customer whose cart should be retrieved. If unspecified, uses the logged-in customer.
     * @return The cart details of the customer or an error message
     */
    @ShellMethod(value = "Get cart details", key = "get-cart")
    public String getCart(@ShellOption(defaultValue = LOGGED_IN_ID_PLACEHOLDER) String customerEmail) {
        customerEmail = cliSession.tryInjectingCustomerEmail(customerEmail);
        if (customerEmail == null) {
            return "Erreur : Veuillez vous connecter ou spécifier un email de client valide.";
        }
        return webClient.get()
                .uri(BASE_CART_URI + "/" + customerEmail)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(CliCart.class)
                .map(cart -> "Détails du panier :\n" + cart.toString().replaceAll("(?m)^", "\t"))
                .block();
    }
}
