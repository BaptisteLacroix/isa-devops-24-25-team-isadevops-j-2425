package fr.univcotedazur.teamj.kiwicard.interfaces;

import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

/**
 * Création et règlement d'un paiement
 */
public interface IBank {
    PaymentDTO askPayment(PaymentRequestDTO paymentInfo) throws UnreachableExternalServiceException;
}
