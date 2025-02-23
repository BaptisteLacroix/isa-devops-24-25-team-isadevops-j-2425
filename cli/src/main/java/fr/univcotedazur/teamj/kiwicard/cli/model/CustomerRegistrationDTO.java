package fr.univcotedazur.teamj.kiwicard.cli.model;

public record CustomerRegistrationDTO(
        String firstName,
        String surname,
        String address,
        String email
) {
}
