package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliItem(long id, String label, double price) {
    @Override
    public String toString() {
        return "• " + id + " : " + (label != null ? label : "no label") + "\t\t" + price + "€";
    }
}
