package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownCustomerEmailException extends Exception {
    public UnknownCustomerEmailException(String email) {
        super("Adresse email inconnue: " + email);
    }
}
