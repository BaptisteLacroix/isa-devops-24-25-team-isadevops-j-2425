package fr.univcotedazur.teamj.kiwicard.interfaces;

import fr.univcotedazur.teamj.kiwicard.dto.CardCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CardDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

/**
 * Demande de création et d'envoi de carte multi-fidélité
 */
public interface ICardCreation {
    CardDTO orderACard(String email, String address) throws UnreachableExternalServiceException;
}
