package fr.univcotedazur.teamj.kiwicard.interfaces.monitoring;

import fr.univcotedazur.teamj.kiwicard.dto.HistoryDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

/**
 * Consultation de l'historique des avantages utilisés par et pour utilisateur
 */
public interface ICustomerHistory {
    HistoryDTO getCustomerHistory(String customerEmail) throws UnknownCustomerEmailException;
}
