package fr.univcotedazur.teamj.kiwicard.cli.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.Map;

public class CliTwoDaysAggregation {
    private Map<String, Integer> day1Aggregation;
    private Map<String, Integer> day2Aggregation;

    // Getters et Setters
    public Map<String, Integer> getDay1Aggregation() {
        return day1Aggregation;
    }

    public void setDay1Aggregation(Map<String, Integer> day1Aggregation) {
        this.day1Aggregation = day1Aggregation;
    }

    public Map<String, Integer> getDay2Aggregation() {
        return day2Aggregation;
    }

    public void setDay2Aggregation(Map<String, Integer> day2Aggregation) {
        this.day2Aggregation = day2Aggregation;
    }
}
