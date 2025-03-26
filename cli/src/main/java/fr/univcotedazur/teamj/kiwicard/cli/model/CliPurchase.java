package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliPurchase(String email, CliCart cartDTO, CliPayment paymentDTO) {
    @Override
    public String toString() {
        return "Propriétaire du panier : " + email + "\n" +
                "Panier : " + this.cartDTO + "\n" +
                "Paiement : " + paymentDTO;

    }
}
