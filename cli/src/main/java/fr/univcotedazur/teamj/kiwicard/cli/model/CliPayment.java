package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliPayment(String cardNumber, double amount, boolean authorized){
    @Override
    public String toString() {
        return  "cardNumber='" + cardNumber + '\'' +
                ", amount=" + amount +
                ", authorized=" + authorized +
                '}';
    }
}

