package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownPartnerIdException extends Exception {
    public UnknownPartnerIdException(long partnerId) {
        super("Partner with id " + partnerId + " not found");
    }
}
