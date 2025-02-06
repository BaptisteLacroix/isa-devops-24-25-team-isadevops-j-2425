package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.HistoryDTO;
import fr.univcotedazur.teamj.kiwicard.dto.StatisticsDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.monitoring.CustomerHistory;
import fr.univcotedazur.teamj.kiwicard.interfaces.monitoring.PartnerHistory;
import fr.univcotedazur.teamj.kiwicard.interfaces.monitoring.StatisticsExplorator;

import java.time.LocalDateTime;

public class PersonalMonitoringService implements CustomerHistory, PartnerHistory, StatisticsExplorator {
    @Override
    public HistoryDTO getCustomerHistory(String customerEmail) throws UnknownCustomerEmailException {
        return null;
    }

    @Override
    public HistoryDTO getPartnerHistory(long partnerId) throws UnknownPartnerIdException {
        return null;
    }

    @Override
    public StatisticsDTO getStatisticsFromUserAndPartner(long partnerId, String customerEmail) throws UnknownPartnerIdException, UnknownCustomerEmailException {
        return null;
    }

    @Override
    public StatisticsDTO getStatisticsFromPartnerAndDate(long partnerId, LocalDateTime date) throws UnknownPartnerIdException {
        return null;
    }
}
