package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownItemIdException extends Exception {
    public UnknownItemIdException(long itemId, String partnerName) {
        super("Item with id "+itemId+" not found in the catalog of " + partnerName);
    }
    public UnknownItemIdException(Long itemId) {
        super("Item with id "+itemId+" not found in catalog");
    }
}
