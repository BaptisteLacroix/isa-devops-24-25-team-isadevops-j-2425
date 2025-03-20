package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Purchase;

public record PurchaseHistoryDTO(CartDTO cartDTO, PaymentHistoryDTO paymentDTO) {
    public PurchaseHistoryDTO(Purchase purchase) {
        this(
                new CartDTO(purchase.getCart()),
                new PaymentHistoryDTO(purchase.getPayment())
        );
    }
}
