package fr.univcotedazur.teamj.kiwicard.dto;

public record PurchaseDTO(String cartOwnerEmail, CartDTO cartDTO, PaymentDTO paymentDTO) {
}
