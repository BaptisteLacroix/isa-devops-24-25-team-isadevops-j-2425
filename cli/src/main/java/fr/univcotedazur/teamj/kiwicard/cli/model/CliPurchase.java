package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliPurchase(String cartOwnerEmail, CliCart cartDTO, CliPayment paymentDTO) {
    @Override
    public String toString() {
        return "Propriétaire du panier : " + cartOwnerEmail + "\n" +
                "Panier : " + this.cartDTO + "\n" +
                "Paiement : " + paymentDTO;

    }
}
