package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliPartner(long id, String name, String address) {
    @Override
    public String toString() {
        return "• " + id + " : " + name + " au " + address;
    }
}
