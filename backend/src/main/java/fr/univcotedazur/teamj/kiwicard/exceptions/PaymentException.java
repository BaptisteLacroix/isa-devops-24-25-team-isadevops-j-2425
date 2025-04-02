package fr.univcotedazur.teamj.kiwicard.exceptions;

public class PaymentException extends Exception {

    private final String name;
    private final double amount;

    public PaymentException(String customerName, double amount) {
        this.name = customerName;
        this.amount = amount;
    }


    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

}
