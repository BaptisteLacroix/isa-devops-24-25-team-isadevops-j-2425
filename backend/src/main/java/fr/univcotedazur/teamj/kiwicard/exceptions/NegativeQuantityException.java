package fr.univcotedazur.teamj.kiwicard.exceptions;


public class NegativeQuantityException extends Exception {

    private String name;
    private int potentialQuantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public int getPotentialQuantity() {
        return potentialQuantity;
    }

    public void setPotentialQuantity(int potentialQuantity) {
        this.potentialQuantity = potentialQuantity;
    }

    public NegativeQuantityException() {
    }

    public NegativeQuantityException(String name, int potentialQuantity) {
        this.name = name;
        this.potentialQuantity = potentialQuantity;
    }
}
