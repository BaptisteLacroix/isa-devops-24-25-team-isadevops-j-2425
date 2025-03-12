package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.util.List;
import java.util.Set;

public record CliCart(long cartId, CliPartner partner, Set<CliCartItem> items, List<CliPerk> perksList) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Cart ID: ").append(cartId).append("\n\t");
        sb.append("Partner: ").append(partner).append("\n\t");

        sb.append("Items in Cart:\n");
        if (items.isEmpty()) {
            sb.append("No items in the cart.\n");
        } else {
            for (CliCartItem item : items) {
                sb.append("\t\t").append(item).append("\n");
            }
        }

        if (!perksList.isEmpty()) {
            sb.append("Perks:\n");
            for (CliPerk perk : perksList) {
                sb.append("\t\t").append(perk).append("\n");
            }
        } else {
            sb.append("\tNo perks applied.\n");
        }

        return sb.toString();
    }
}
