package fr.univcotedazur.teamj.kiwicard.cli.model;

public class CartElement {

    private int quantity;



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CartElement() {
    }

    public CartElement(int howMany) {
        this.quantity = howMany;
    }

    @Override
    public String toString() {
        return quantity + "x";
    }

}
