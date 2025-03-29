package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.util.List;

public record CliHistoryCart(long cartId, CliPartner partner, List<CliHistoryCartItem> items, List<CliPerk> perksList) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Panier : ").append("\n\t");
        sb.append("Partenaire : ").append(partner).append("\n\t");

        sb.append("Article(s) dans le panier : \n");
        if (items.isEmpty()) {
            sb.append("Panier vide : Pas d'articles dans le panier.\n");
        } else {
            for (CliHistoryCartItem item : items) {
                sb.append("\t\t").append(item).append("\n");
            }
        }

        if (!perksList.isEmpty()) {
            sb.append("Avantages : \n");
            for (CliPerk perk : perksList) {
                sb.append("\t\t").append(perk).append("\n");
            }
        } else {
            sb.append("\tPas d'avantage(s) appliqué(s).\n");
        }

        return sb.toString();
    }
}
