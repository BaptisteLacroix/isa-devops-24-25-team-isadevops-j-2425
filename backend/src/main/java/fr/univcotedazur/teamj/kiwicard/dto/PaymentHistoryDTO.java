package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Payment;

import java.time.LocalDateTime;

public record PaymentHistoryDTO(double amount, LocalDateTime timestamp) {
    public PaymentHistoryDTO(Payment entity) {
        this(
                entity.getAmount(),
                entity.getTimestamp()
        );
    }
}
