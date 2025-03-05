package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliCustomer(
        String firstname,
        String surname,
        String address,
        String email,
        boolean vfp) {
    @Override
    public String toString() {
        return "• " + email + " : " + firstname + " " + surname + " au " + address + " (VFP: " + vfp + ")";
    }
}
