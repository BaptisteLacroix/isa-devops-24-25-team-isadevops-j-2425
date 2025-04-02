package fr.univcotedazur.teamj.kiwicard.exceptions;

public class EmptyCartException extends Exception {
    public EmptyCartException(Long id) {
        super("Le panier avec l'id " + id + " est vide");
    }
}
