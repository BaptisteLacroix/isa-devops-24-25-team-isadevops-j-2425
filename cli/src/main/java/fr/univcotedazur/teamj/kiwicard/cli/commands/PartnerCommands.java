package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCart;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliCartItem;
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
    @ShellMethod("""
            Show all partners (partners)
            """)
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
            Show items of a partner
            """, key = "partner-items")
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
     * Consults the perks of a partner based on the partner's ID.
     * <p>
     * Example usage:
     * consult-partner-perks --partnerId <partnerId>
     *
     * @param partnerId The ID of the partner whose perks are to be consulted.
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

    /**
     * Prints the perks of a partner in a formatted manner.
     *
     * @param perks A list of perks to be displayed.
     */
    private void printPerks(List<CliPerk> perks) {
        if (perks.isEmpty()) {
            System.out.println("No perks available for this partner.");
            return;
        }
        System.out.println("List of Perks:\n");
        perks.forEach(System.out::println);
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
        if (itemId == null || quantity == null || quantity <= 0) {
            System.out.println("Error: itemId and quantity must be provided, and quantity must be greater than zero.");
            return;
        }

        CliCartItem cartItemDTO = new CliCartItem(quantity, null, null, itemId);
        CliCart updatedCart = sendCartRequest(customerEmail, cartItemDTO);

        if (updatedCart != null) {
            System.out.println("Item successfully added to the cart:");
            System.out.println(updatedCart);
        } else {
            System.out.println("Failed to add the item to the cart.");
        }
    }

    /**
     * Sends a request to update a customer's cart.
     *
     * @param customerEmail The email address of the customer whose cart is being updated.
     * @param cartItemDTO   The details of the cart item being added or updated.
     * @return The updated cart.
     */
    private CliCart sendCartRequest(String customerEmail, CliCartItem cartItemDTO) {
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

