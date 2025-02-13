package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Same DTO as input and output (no id in the input)
public record CustomerDTO (

        @NotBlank
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String surname,

        @Pattern(regexp = "true|false")
        String vfp
) {
    public CustomerDTO(Customer customer) {
        this(customer.getEmail(), customer.getFirstName(), customer.getSurname(), customer.isVfp() ? "true" : "false");
    }
}
