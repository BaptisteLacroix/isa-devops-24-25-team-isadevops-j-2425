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

    @ShellMethod(key = "Aggregate Purchases", value = "aggregate-purchases")
    public String aggregatePurchases(@ShellOption String partnerId, @ShellOption String day1, @ShellOption String day2, @ShellOption(defaultValue = "60") String duration) throws JsonProcessingException {
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

    @ShellMethod(key = "Aggregate Perks", value = "aggregate-perks")
    public String aggregatePerks(@ShellOption String partnerId) throws JsonProcessingException {
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
