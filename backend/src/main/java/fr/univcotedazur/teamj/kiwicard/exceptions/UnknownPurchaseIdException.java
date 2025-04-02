package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownPurchaseIdException extends Exception {
    public UnknownPurchaseIdException(long purchaseId) {
        super("Purchase with id " + purchaseId + " not found");
    }
}
