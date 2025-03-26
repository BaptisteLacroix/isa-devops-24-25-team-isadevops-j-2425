package fr.univcotedazur.teamj.kiwicard.cli.commands;

import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliHistoryCart;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliHistoryPayment;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliHistoryPurchase;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.KiwiCardQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static fr.univcotedazur.teamj.kiwicard.cli.constants.Constants.LOGGED_IN_ID_PLACEHOLDER;

@ShellComponent
public class MonitoringCommands {


    private static final String BASE_URI = "/monitoring";
    private final WebClient webClient;

    private final CliSession cliSession;

    @Autowired
    public MonitoringCommands(WebClient webClient, CliSession cliSession) {
        this.webClient = webClient;
        this.cliSession = cliSession;
    }

    @ShellMethod("""
            
                Get customer history:
                Usage: customer-history --email <email> --limit <limit>
            
                Parameters:
                    --email/-e   The email address of the customer. If not provided, the logged-in customer's email will be used.
                    --limit/-l   The maximum number of history records to retrieve. Optional.
            
                Example:
                    customer-history --email alice.bob@gmail.com --limit 10
            """)
    public String customerHistory(@ShellOption(value = {"-e", "--email"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER) String customerEmail,
                                     @ShellOption(value = {"-l", "--limit"}, defaultValue = "") String limit) {
        customerEmail = cliSession.tryInjectingCustomerEmail(customerEmail);
        if (customerEmail == null) return "Erreur : Veuillez vous connecter ou spécifier un email de client valide.";
        try {
            if (!limit.isEmpty()) Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            return "Erreur : La limite spécifiée n'est pas un nombre valide.";
        }
        String finalCustomerEmail = customerEmail;
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(BASE_URI + "/customer/" + finalCustomerEmail + "/history");
                    if (!limit.isEmpty()) {
                        uriBuilder.queryParam("limit", limit);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new KiwiCardQueryException(error.errorMessage())))
                )
                .bodyToFlux(CliHistoryPurchase.class)
                .collectList()
                .map(historyList -> formatPurchaseHistory(historyList, finalCustomerEmail)).block();
    }

    private static String formatPurchaseHistory(List<CliHistoryPurchase> historyList, String finalCustomerEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("Historique du client ").append(finalCustomerEmail).append(" :\n");
        String lineTemplate = "\t%-20s | %-20s | %-20s | %-10s | %-20s%n";
        sb.append(String.format(lineTemplate, "Date", "Commerçant", "Articles", "Total payé", "Avantages"));
        sb.append("\t----------------------------------------------------------------------------------------------------\n");
        for (CliHistoryPurchase history : historyList) {
            CliHistoryCart cart = history.cartDTO();
            CliHistoryPayment payment = history.paymentDTO();
            int greatestList = Math.max(cart.items().size(), cart.perksList().size());
            for (int i = 0; i < greatestList; i++) {
                String itemStr = i < cart.items().size() ? cart.items().get(i).toString() : "";
                String perkStr = i < cart.perksList().size() ? cart.perksList().get(i).toString() : "";
                if (i == 0) {
                    sb.append(String.format(lineTemplate,
                            payment.timestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")),
                            cart.partner().name(),
                            itemStr,
                            String.format(Locale.FRANCE, "%,.2f", payment.amount()),
                            perkStr));
                } else {
                    sb.append(String.format(lineTemplate,
                            "",
                            "",
                            itemStr,
                            "",
                            perkStr));
                }
            }
            sb.append("\t----------------------------------------------------------------------------------------------------\n");
        }
        return sb.toString();
    }
}
