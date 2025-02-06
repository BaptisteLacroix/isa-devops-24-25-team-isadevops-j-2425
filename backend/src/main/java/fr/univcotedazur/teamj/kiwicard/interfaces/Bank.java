package fr.univcotedazur.teamj.kiwicard.interfaces;

import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

import java.util.Optional;

/**
 * Création et règlement d'un paiement
 */
public interface Bank {
    PaymentDTO askPayment(PaymentRequestDTO paymentInfo) throws UnreachableExternalServiceException;
}
