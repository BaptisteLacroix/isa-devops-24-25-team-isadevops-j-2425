package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliPerk(long perkId, String description) {
    @Override
    public String toString() {
        return "Perk ID: " + perkId + "\nDescription: " + description + "\n";
    }
}
