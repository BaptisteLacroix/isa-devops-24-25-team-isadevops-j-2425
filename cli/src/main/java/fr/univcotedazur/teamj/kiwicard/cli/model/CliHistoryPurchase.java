package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliHistoryPurchase(CliHistoryCart cartDTO, CliHistoryPayment paymentDTO) {
    @Override
    public String toString() {
        return "Panier : " + this.cartDTO + "\n" +
                "Paiement : " + paymentDTO;

    }
}
