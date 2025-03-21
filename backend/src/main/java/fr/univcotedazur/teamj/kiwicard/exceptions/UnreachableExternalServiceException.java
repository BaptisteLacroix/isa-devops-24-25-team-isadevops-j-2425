package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnreachableExternalServiceException extends Exception {
    public UnreachableExternalServiceException(String message) {
        super("Unreachable external service at " + message);
    }

    public UnreachableExternalServiceException() {
        super("Unreachable external service");
    }
}
