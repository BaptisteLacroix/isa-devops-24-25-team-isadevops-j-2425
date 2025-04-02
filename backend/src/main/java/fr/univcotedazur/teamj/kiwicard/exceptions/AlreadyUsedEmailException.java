package fr.univcotedazur.teamj.kiwicard.exceptions;

public class AlreadyUsedEmailException extends Exception {
    public AlreadyUsedEmailException(String email) {
        super("L'email " + email + " est déjà utilisé");
    }
}
