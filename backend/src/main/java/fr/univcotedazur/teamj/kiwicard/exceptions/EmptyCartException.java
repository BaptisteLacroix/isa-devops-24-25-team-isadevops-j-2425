package fr.univcotedazur.teamj.kiwicard.exceptions;

public class EmptyCartException extends Exception {
    public EmptyCartException(Long id) {
        super("Cart with id " + id + " is empty");
    }
}
