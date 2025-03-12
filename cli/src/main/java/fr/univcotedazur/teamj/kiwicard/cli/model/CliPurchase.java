package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliPurchase(String cartOwnerEmail, CliCart cartDTO, CliPayment paymentDTO) {
    @Override
    public String toString() {
        return  "owner email : " + cartOwnerEmail + "\n" +
                "cart : " + this.cartDTO + "\n" +
                "payment : " + paymentDTO;

    }
}
