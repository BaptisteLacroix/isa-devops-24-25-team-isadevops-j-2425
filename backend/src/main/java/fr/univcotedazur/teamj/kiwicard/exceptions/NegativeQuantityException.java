package fr.univcotedazur.teamj.kiwicard.exceptions;


public class NegativeQuantityException extends Exception {

    private final String name;
    private final int potentialQuantity;

    public String getName() {
        return name;
    }

    public int getPotentialQuantity() {
        return potentialQuantity;
    }

    public NegativeQuantityException(String name, int potentialQuantity) {
        this.name = name;
        this.potentialQuantity = potentialQuantity;
    }
}
