package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownCustomerEmailException extends Exception {
    public UnknownCustomerEmailException(String email) {
        super("Unknown customer email: " + email);
    }
}
