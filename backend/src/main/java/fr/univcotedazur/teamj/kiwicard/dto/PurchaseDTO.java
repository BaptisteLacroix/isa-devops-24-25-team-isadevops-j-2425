package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Purchase;

public record PurchaseDTO(String email, CartDTO cartDTO, PaymentDTO paymentDTO) {
}
