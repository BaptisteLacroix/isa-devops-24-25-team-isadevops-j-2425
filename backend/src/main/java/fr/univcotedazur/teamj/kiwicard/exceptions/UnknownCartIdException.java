package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownCartIdException extends Exception {
    public UnknownCartIdException(Long id) {
        super("Cart with id " + id + " not found");
    }
}
