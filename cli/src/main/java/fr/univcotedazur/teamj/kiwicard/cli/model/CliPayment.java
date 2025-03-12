package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliPayment(String cardNumber, double amount, boolean authorized){
    @Override
    public String toString() {
        return "Numéro de carte ='" + cardNumber + '\'' +
                ", montant =" + amount +
                ", autorisation =" + authorized +
                '}';
    }
}

