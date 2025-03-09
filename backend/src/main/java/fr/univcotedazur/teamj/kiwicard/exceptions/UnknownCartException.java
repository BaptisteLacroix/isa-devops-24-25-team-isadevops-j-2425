package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownCartException extends Exception {
    public UnknownCartException() {
        super("The user does not have any cart");
    }
}
