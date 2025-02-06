package fr.univcotedazur.teamj.kiwicard.interfaces.monitoring;

import fr.univcotedazur.teamj.kiwicard.dto.HistoryDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

/**
 * Consultation de l'historique des avantages utilisés chez un partenaire
 */
public interface PartnerHistory {
    HistoryDTO getPartnerHistory(long partnerId) throws UnknownPartnerIdException;
}
