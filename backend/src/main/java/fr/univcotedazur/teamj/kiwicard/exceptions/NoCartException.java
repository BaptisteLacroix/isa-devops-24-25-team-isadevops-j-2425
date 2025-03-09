package fr.univcotedazur.teamj.kiwicard.exceptions;

public class NoCartException extends RuntimeException {
    public NoCartException(String customerEmail) {
        super("No cart found for customer with email " + customerEmail);
    }
}
