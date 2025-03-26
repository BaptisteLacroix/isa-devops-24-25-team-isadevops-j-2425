package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {CustomerController.class, CartController.class, PartnerController.class, PerksController.class})
public class GlobalControllerAdvice {

    @ExceptionHandler({UnreachableExternalServiceException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDTO handleExceptions(UnreachableExternalServiceException e) {
        return new ErrorDTO("External service is unreachable");
    }

    @ExceptionHandler({NegativeQuantityException.class, EmptyCartException.class, BookingTimeNotSetException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleExceptions(NegativeQuantityException e) {
        return new ErrorDTO(e.getMessage());
    }

    @ExceptionHandler({PaymentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleExceptions(PaymentException e) {
        return new ErrorDTO("Payment was rejected from Customer " + e.getName() + " for totalPrice " + e.getAmount());
    }

    @ExceptionHandler({UnknownPartnerIdException.class, UnknownItemIdException.class, UnknownCustomerEmailException.class, UnknownCartIdException.class, UnknownPerkIdException.class, NoCartException.class, UnknownCardNumberException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleExceptions(Exception e) {
        return new ErrorDTO(e.getMessage());
    }

    @ExceptionHandler({AlreadyUsedEmailException.class,AlreadyBookedTimeException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleAlreadyUsedEmail(AlreadyUsedEmailException ex) {
        return new ErrorDTO(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorDTO(ex.getMessage());
    }
}
