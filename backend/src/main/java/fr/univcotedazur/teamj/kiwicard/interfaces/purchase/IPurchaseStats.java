package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public interface IPurchaseStats {
    Map<LocalTime, Integer> aggregateByDayAndDuration(long partnerId, LocalDate day, Duration separation);
}
