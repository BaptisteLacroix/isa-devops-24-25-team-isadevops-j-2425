package fr.univcotedazur.teamj.kiwicard.interfaces;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

/**
 * Création et réglement d'un paiment
 */
public interface Payment {
    PaymentDTO makePay(CartDTO cartToPay) throws UnreachableExternalServiceException;
}
