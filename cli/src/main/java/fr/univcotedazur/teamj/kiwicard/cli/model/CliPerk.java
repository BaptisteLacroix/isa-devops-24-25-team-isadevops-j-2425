package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliPerk(long id, String description) {
    @Override
    public String toString() {
        return "• " + id + " : " + description;
    }
}
