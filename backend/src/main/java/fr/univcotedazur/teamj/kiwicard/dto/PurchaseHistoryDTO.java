package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Purchase;

public record PurchaseHistoryDTO(CartInPurchaseDTO cartDTO, PaymentHistoryDTO paymentDTO) {
    public PurchaseHistoryDTO(Purchase purchase) {
        this(
                new CartInPurchaseDTO(purchase.getCart()),
                new PaymentHistoryDTO(purchase.getPayment())
        );
    }
}
