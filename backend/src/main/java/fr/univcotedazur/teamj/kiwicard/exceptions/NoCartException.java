package fr.univcotedazur.teamj.kiwicard.exceptions;

public class NoCartException extends Exception {
    public NoCartException(String customerEmail) {
        super("Aucun panier trouvé pour le client avec l'email " + customerEmail);
    }
}
