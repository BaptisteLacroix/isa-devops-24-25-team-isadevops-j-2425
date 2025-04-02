package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public interface IPurchaseStats {
    Map<LocalTime, Integer> aggregatePurchasesByDayAndDuration(long partnerId, LocalDate day, Duration separation) throws UnknownPartnerIdException;
}
