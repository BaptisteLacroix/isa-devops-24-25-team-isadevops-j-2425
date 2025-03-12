package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliItem(long itemId, String label, double price) {
    @Override
    public String toString() {
        return "• " + itemId + " : " + label + "\t\t" + price + "€";
    }
}
