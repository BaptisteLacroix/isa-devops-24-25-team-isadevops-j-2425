package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.time.LocalDateTime;

public record CliHistoryCartItem(
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        CliItem item
) {
    @Override
    public String toString() {
        return String.format("%d %s %s %.2f€",
                quantity(), item().label(),
                (startTime() != null && endTime() != null) ? String.format(", Horaires: [%s - %s]", startTime(), endTime()) : "",
                item().price()*quantity());
    }
}
