package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCart;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCartItemToSent;
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
    public static final String CART_BASE_URI = "/cart";

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
            Example: partners
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
            Usage: partner-items --partnerId <partnerId>
            Parameters:
                --partnerId  The ID of the partner whose items you want to display.
            Example: partner-items --partnerId 12345
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

    /**
     * Adds an item to a customer's shopping cart.
     * <p>
     * Example usage:
     * add-item-to-cart --customer-email <customerEmail> --itemId <itemId> --quantity <quantity>
     *
     * @param customerEmail The email of the customer to whom the cart belongs.
     * @param itemId        The ID of the item to be added to the cart.
     * @param quantity      The quantity of the item to be added to the cart.
     */
    @ShellMethod("""
                Add an item to a customer's cart:
                Usage: add-item-to-cart --customer-email <customerEmail> --item-id <itemId> --quantity <quantity>
            
                Parameters:
                    --customer-email/-e  The email of the customer to whom the cart belongs.
                    --item-id/-i         The ID of the item to be added to the cart.
                    --quantity/-q        The quantity of the item to add to the cart.
            
                Example:
                    add-item-to-cart --customer-email "customer@example.com" --item-id 123 --quantity 2
            """)
    public String addItemToCart(
            @ShellOption(value = {"-e", "--customer-email"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER) String customerEmail,
            @ShellOption(value = {"-i", "--item-id"}) Long itemId,
            @ShellOption(value = {"-q", "--quantity"}) Integer quantity
    ) {
        customerEmail = cliSession.tryInjectingCustomerEmail(customerEmail);
        if (customerEmail == null) return "Erreur : Veuillez vous connecter ou spécifier un email de client valide.";
        checkQuantity(quantity);
        CliCartItemToSent cartItemDTO = new CliCartItemToSent(quantity, null, itemId);
        CliCart updatedCart = sendCartRequest(customerEmail, cartItemDTO);

        if (updatedCart != null) {
            System.out.println("Article ajouté au panier avec succès :");
            return "" + updatedCart;
        } else {
            return "Erreur lors de l'ajout de l'article au panier.";
        }
    }

    /**
     * Reserves a time slot for a customer's cart.
     * <p>
     * Example usage:
     * reserve-time-slot --customer-email <customerEmail> --start-time <startTime> --endTime <endTime> --quantity <quantity> --itemId <itemId>
     *
     * @param customerEmail The email of the customer to whom the cart belongs.
     * @param startTime     The start time for the time slot.
     * @param quantity      The quantity of the item to be added to the cart.
     * @param itemId        The ID of the item to be added to the cart.
     */
    @ShellMethod("""
                Reserve a time slot for a customer:
                Usage: reserve-time-slot --customer-email <customerEmail> --start-time <startTime> --endTime <endTime> --quantity <quantity> --itemId <itemId>
            
                Parameters:
                    --customer-email/-e  The email of the customer to whom the cart belongs.
                    --start-time      The start time for the item in the cart.
                    --quantity/-q        The quantity of the item to add to the cart.
                    --item-id/-i      The ID of the item to be added to the cart.
            
                Example:
                    reserve-time-slot --customer-email "customer@example.com" --start-time "2025-03-12T10:00:00" --quantity 2 --item-id 123
            """)
    public void reserveTimeSlot(
            @ShellOption(value = {"-e", "--customer-email"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER) String customerEmail,
            @ShellOption(value = {"-i", "--item-id"}) Long itemId,
            LocalDateTime startTime,
            @ShellOption(value = {"-q", "--quantity"}) Integer quantity
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

    /**
     * Sends a request to update a customer's cart.
     *
     * @param customerEmail The email address of the customer whose cart is being updated.
     * @param cartItemDTO   The details of the cart item being added or updated.
     * @return The updated cart.
     */
    private CliCart sendCartRequest(String customerEmail, CliCartItemToSent cartItemDTO) {
        return webClient.put()
                .uri(CART_BASE_URI + "/" + customerEmail)
                .bodyValue(cartItemDTO)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(CliCart.class)
                .block();
    }
}

