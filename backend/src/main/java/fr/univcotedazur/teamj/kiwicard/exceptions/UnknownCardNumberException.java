package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownCardNumberException extends Exception {
    public UnknownCardNumberException(String cardNumber) {
        super("Numéro de carte inconnu: " + cardNumber);
    }
}
