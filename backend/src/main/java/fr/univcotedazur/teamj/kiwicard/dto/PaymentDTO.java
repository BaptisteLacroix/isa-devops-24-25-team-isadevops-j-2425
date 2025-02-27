package fr.univcotedazur.teamj.kiwicard.dto;

public record PaymentDTO(String cardNumber, double amount, boolean authorized) {
}

