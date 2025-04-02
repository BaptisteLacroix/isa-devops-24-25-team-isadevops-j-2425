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
    private static final String SPLIT_LINE = "\t----------------------------------------------------------------------------------------------------\n";

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
                .map(historyList -> formatCustomerPurchaseHistory(historyList, finalCustomerEmail)).block();
    }

    private static String formatCustomerPurchaseHistory(List<CliHistoryPurchase> historyList, String customerEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("Historique du client ").append(customerEmail).append(" :\n");
        String lineTemplate = "\t%-20s | %-20s | %-20s | %-10s | %-20s%n";
        sb.append(String.format(lineTemplate, "Date", "Commerçant", "Articles", "Total payé", "Avantages"));
        sb.append(SPLIT_LINE);
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
                            String.format(Locale.FRANCE, "%,.2f€", payment.amount()),
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
            sb.append(SPLIT_LINE);
        }
        return sb.toString();
    }

    @ShellMethod("""
            
            Get partner history:
                Usage: partner-history --partner-id <partner-id> --limit <limit>
            
                Parameters:
                    --partner-id/-p   The ID of the partner. If not provided, the logged-in partner's ID will be used.
                    --limit/-l        The maximum number of history records to retrieve. Optional.
            
                Example:
                    partner-history --partner-id 12345 --limit 10
            """)
    public String partnerHistory(@ShellOption(value = {"-p", "--partner-id"}, defaultValue = LOGGED_IN_ID_PLACEHOLDER) String partnerId,
                                 @ShellOption(value = {"-l", "--limit"}, defaultValue = "") String limit) {
        partnerId = cliSession.tryInjectingPartnerId(partnerId);
        if (partnerId == null)
            return "Erreur : Veuillez vous connecter ou spécifier un identifiant de partenaire valide.";
        try {
            if (!limit.isEmpty()) Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            return "Erreur : La limite spécifiée n'est pas un nombre valide.";
        }
        String finalPartnerId = partnerId;
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(BASE_URI + "/partner/" + finalPartnerId + "/history");
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
                .map(MonitoringCommands::formatPartnerPurchaseHistory).block();
    }

    private static String formatPartnerPurchaseHistory(List<CliHistoryPurchase> historyList) {
        if (historyList.isEmpty()) return "Aucune ventes trouvée pour ce partenaire.";
        StringBuilder sb = new StringBuilder();
        String partnerName = historyList.getFirst().cartDTO().partner().name();
        sb.append("Historique du partenaire ").append(partnerName).append(" :\n");
        String lineTemplate = "\t%-20s | %-20s | %-10s | %-20s%n";
        sb.append(String.format(lineTemplate, "Date", "Articles", "Total payé", "Avantages"));
        sb.append(SPLIT_LINE);
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
                            itemStr,
                            String.format(Locale.FRANCE, "%,.2f€", payment.amount()),
                            perkStr));
                } else {
                    sb.append(String.format(lineTemplate,
                            "",
                            itemStr,
                            "",
                            perkStr));
                }
            }
            sb.append(SPLIT_LINE);
        }
        return sb.toString();
    }


}
