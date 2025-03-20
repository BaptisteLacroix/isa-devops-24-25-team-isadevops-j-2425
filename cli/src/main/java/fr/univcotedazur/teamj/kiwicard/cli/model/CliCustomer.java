package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CliCustomer(
        String email,
        String firstname,
        String surname,
        String address,
        boolean vfp,
        CliCart cart,
        String creditCard) {
    @Override
    public String toString() {
        return "• " + email + " : " + firstname + " " + surname + " au " + address + " (VFP: " + vfp + ")";
    }
}
