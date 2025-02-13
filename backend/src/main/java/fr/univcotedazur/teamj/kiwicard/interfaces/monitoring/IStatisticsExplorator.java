package fr.univcotedazur.teamj.kiwicard.interfaces.monitoring;

import fr.univcotedazur.teamj.kiwicard.dto.StatisticsDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

import java.time.LocalDateTime;

/**
 * Consultation des statistiques d'un partenaire
 */
public interface IStatisticsExplorator {
    StatisticsDTO getStatisticsFromUserAndPartner (long partnerId, String customerEmail) throws UnknownPartnerIdException, UnknownCustomerEmailException;
    StatisticsDTO getStatisticsFromPartnerAndDate (long partnerId, LocalDateTime date) throws UnknownPartnerIdException;
}
