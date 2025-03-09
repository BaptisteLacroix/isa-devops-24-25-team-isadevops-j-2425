package fr.univcotedazur.teamj.kiwicard.exceptions;

public class NoCartException extends Exception {
    public NoCartException(String customerEmail) {
        super("No cart found for customer with email " + customerEmail);
    }
}
