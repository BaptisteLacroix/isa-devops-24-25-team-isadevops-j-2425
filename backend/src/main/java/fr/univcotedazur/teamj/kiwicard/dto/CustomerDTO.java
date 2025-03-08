package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import jakarta.validation.constraints.NotBlank;

// Same DTO as input and output (no id in the input)
public record CustomerDTO(
        @NotBlank String email,
        @NotBlank String firstName,
        @NotBlank String surname,
        boolean vfp,
        CartDTO cartDTO,
        String creditCard) {
    public CustomerDTO(Customer customer) {
        this(
                customer.getEmail(),
                customer.getFirstName(),
                customer.getSurname(),
                customer.isVfp(),
                // If the customer has no cart, the cartDTO is null
                customer.getCart() == null ? null : new CartDTO(customer.getCart()),
                customer.getCardNumber()
        );
    }
}
