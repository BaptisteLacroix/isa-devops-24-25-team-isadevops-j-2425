package fr.univcotedazur.teamj.kiwicard.cli.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.cli.CliSession;
import fr.univcotedazur.teamj.kiwicard.cli.model.CliTwoDaysAggregation;
import fr.univcotedazur.teamj.kiwicard.cli.model.error.CliError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@ShellComponent
public class StatsCommands {
    private final String MONITORING_BASE_URI = "/monitoring";
    public final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final CliSession cliSession;
    private final WebClient webClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public StatsCommands(WebClient webClient, CliSession cliSession) {
        this.cliSession = cliSession;
        this.webClient = webClient;
    }

    /**
     * Aggregates the number of purchases for each hour of the day for two specific days for a specific partner.
     * <p>
     * Example usage:
     * aggregate-purchases --partner-id <partnerId> --day-one <day1> --day-two <day2> --duration <duration>
     *
     * @param partnerId The ID of the partner whose purchases are to be aggregated.
     * @param day1 The first day for which the purchases are to be aggregated.
     * @param day2 The second day for which the purchases are to be aggregated.
     * @param duration The duration in minutes for which the purchases are to be aggregated.
     * @return A string representing the aggregated purchases for each hour of the day for the two specified days.
     * @throws JsonProcessingException when the API returns a bad response.
     */
    @ShellMethod(value = """
        Aggregate the number of purchases for each hour of the day for two specific days for a specific partner.
        Usage: aggregate-purchases --partner-id <partnerId> --day-one <day1> --day-two <day2> --duration <duration>
        Parameters:
            --partner-id  The ID of the partner whose purchases you want to aggregate.
            --day-one     The first day for which the purchases are to be aggregated.
            --day-two     The second day for which the purchases are to be aggregated.
            --duration    The duration in minutes for which the purchases are to be aggregated.
        Example: aggregate-purchases --partner-id 12345 --day-one 2025-03-16 --day-two 2025-04-16 --duration 60
        """, key = "aggregate-purchases")
    public String aggregatePurchases(
            @ShellOption(value = {"-p", "--partner-id"}) String partnerId,
            @ShellOption(value = {"-d1", "--day-one"}) String day1,
            @ShellOption(value = {"-d2", "--day-two"}) String day2,
            @ShellOption(value = {"-d", "--duration"}, defaultValue = "60") String duration) throws JsonProcessingException {
        try {
            LocalDate.parse(day1, dateFormatter);
            LocalDate.parse(day2, dateFormatter);
        } catch (Exception e) {
            return "Error: dates must be of pattern " + this.dateFormatter;
        }
        partnerId = cliSession.tryInjectingPartnerId(partnerId);
        if (partnerId == null) return "Erreur : ID de partenaire invalide.";

        String finalPartnerId = partnerId;

        String resultString =  makeAggregRequest(day1, day2, duration, finalPartnerId);

        CliTwoDaysAggregation aggs = objectMapper.readValue(resultString, new TypeReference<>(){});
        return "purchases at : " + day1 + " : \n" +
        formatAggregation(aggs.getDay1Aggregation()) + "\n" +
        "purchases at : " + day2 + " : \n" +
        formatAggregation(aggs.getDay2Aggregation()) + "\n";
    }

    private String makeAggregRequest(String day1, String day2, String duration, String finalPartnerId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats/{partnerId}/comparePurchases")
                        .queryParam("day1", day1)
                        .queryParam("day2", day2)
                        .queryParam("duration", duration) // Pas besoin de `Duration.ofMinutes`
                        .build(finalPartnerId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(String.class)
                .map(s -> s + "}")
                .block();
    }

    private String formatAggregation(Map<String, Integer> agg) {
        return agg.entrySet().stream()
                .map(entry-> entry.getKey() + " : " + entry.getValue())
                .reduce((a, b)-> a + "\n" + b).orElseThrow();
    }

    /**
     * Aggregates the number of perks used in a purchase for each perk type for a specific partner.
     * <p>
     * Example usage:
     * aggregate-perks --partner-id <partnerId>
     *
     * @param partnerId The ID of the partner whose perks usage is to be aggregated.
     * @return A string representing the aggregated perks usage by type.
     * @throws JsonProcessingException when the API returns a bad response.
     */
    @ShellMethod(value = """
        Aggregate the number of perks used in a purchase for each perk type for a specific partner.
        Usage: aggregate-perks --partner-id <partnerId>
        Parameters:
            --partner-id  The ID of the partner whose perks usage you want to aggregate.
        Example: aggregate-perks --partner-id 12345
        """, key = "aggregate-perks")
    public String aggregatePerks(@ShellOption(value = {"-p", "--partner-id"}) String partnerId) throws JsonProcessingException {
        partnerId = cliSession.tryInjectingPartnerId(partnerId);
        if (partnerId == null) return "Erreur : ID de partenaire invalide.";

        String finalPartnerId = partnerId;
        String result =  webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats/{partnerId}/nb-perks-by-type")
                        .build(finalPartnerId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(CliError.class)
                        .flatMap(error -> Mono.error(new RuntimeException(error.errorMessage()))))
                .bodyToMono(String.class)
                .block();
        HashMap<String, Integer> aggregation = objectMapper.readValue(result, new TypeReference<>() {});
        return formatAggregation(aggregation);
    }
}
