package fr.univcotedazur.teamj.kiwicard.dto;

public record PurchaseDTO(String email, CartInPurchaseDTO cartDTO, PaymentDTO paymentDTO) {
}
