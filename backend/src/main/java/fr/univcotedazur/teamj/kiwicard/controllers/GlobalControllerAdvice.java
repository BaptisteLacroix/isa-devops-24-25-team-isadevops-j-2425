package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyBookedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.BookingTimeNotSetException;
import fr.univcotedazur.teamj.kiwicard.exceptions.EmptyCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.ForbiddenDurationException;
import fr.univcotedazur.teamj.kiwicard.exceptions.InapplicablePerkException;
import fr.univcotedazur.teamj.kiwicard.exceptions.NegativeQuantityException;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.PaymentException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCartIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {CustomerController.class, CartController.class, PartnerController.class, PerksController.class, MonitoringController.class})
public class GlobalControllerAdvice {

    @ExceptionHandler({UnreachableExternalServiceException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDTO handleExceptions(UnreachableExternalServiceException e) {
        return new ErrorDTO("External service is unreachable");
    }

    @ExceptionHandler({NegativeQuantityException.class, EmptyCartException.class, BookingTimeNotSetException.class, InapplicablePerkException.class, ForbiddenDurationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleForbiddenExceptions(Exception e) {
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

    @ExceptionHandler({AlreadyUsedEmailException.class, AlreadyBookedTimeException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleConflictExceptions(AlreadyUsedEmailException ex) {
        return new ErrorDTO(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorDTO(ex.getMessage());
    }
}
