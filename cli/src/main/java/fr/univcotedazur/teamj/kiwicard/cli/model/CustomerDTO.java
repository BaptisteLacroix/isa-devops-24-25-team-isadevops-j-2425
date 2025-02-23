package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CustomerDTO(
        Long customerId,
        String firstName,
        String surname,
        String address,
        String email,
        boolean vfp) {
}
