package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.NegativeQuantityException;
import fr.univcotedazur.teamj.kiwicard.exceptions.PaymentException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {CustomerController.class, CartController.class, PartnerController.class})
public class GlobalControllerAdvice {

    @ExceptionHandler({NegativeQuantityException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleExceptions(NegativeQuantityException e) {
        return new ErrorDTO("Attempting to update the cookie quantity to a negative value from Customer " + e.getName() +
                        " leading to quantity " + e.getPotentialQuantity());
    }

    @ExceptionHandler({PaymentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleExceptions(PaymentException e) {
        return new ErrorDTO("Payment was rejected from Customer " + e.getName() + " for amount " + e.getAmount());
    }

    @ExceptionHandler({UnknownPartnerIdException.class, UnknownItemIdException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleExceptions(UnknownPartnerIdException e) {
        return new ErrorDTO(e.getMessage());
    }
}
