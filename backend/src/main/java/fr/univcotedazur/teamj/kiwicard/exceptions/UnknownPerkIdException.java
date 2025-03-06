package fr.univcotedazur.teamj.kiwicard.exceptions;

public class UnknownPerkIdException extends Exception {
    public UnknownPerkIdException(Long perkId) {
        super("Unknown perk id: " + perkId);
    }
}
