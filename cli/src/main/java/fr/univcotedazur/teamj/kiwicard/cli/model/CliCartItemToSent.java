package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.time.LocalDateTime;

public record CliCartItemToSent(
        int quantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long itemId
) {
}
