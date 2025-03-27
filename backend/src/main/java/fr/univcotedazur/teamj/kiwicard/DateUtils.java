package fr.univcotedazur.teamj.kiwicard;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DateUtils {
    /**
     * Divides a LocalDate into multiple LocalDateTime with a step
     * @param day The LocalDate that will be divided
     * @param duration The step between two LocalDateTime
     * @return A List of resulting LocalDateTime
     */
    public static List<LocalDateTime> getLocalDateTimes(LocalDate day, Duration duration) {
        LocalDateTime day2 = day.atStartOfDay().plusDays(1);
        List<LocalDateTime> timestamps = IntStream.iterate(1, i -> i+1)
                .mapToObj(i-> LocalTime.ofSecondOfDay(Math.min(LocalTime.MAX.toSecondOfDay(), duration.multipliedBy(i).getSeconds())))
                .map(time->LocalDateTime.of(day, time))
                .takeWhile(dateTime -> dateTime.isBefore(day2.minus(duration)))
                .collect(Collectors.toCollection(ArrayList::new));
        timestamps.add(day2.minus(duration));
        if (!timestamps.contains(day2.minusSeconds(1)))timestamps.add(day2.minusSeconds(1));
        return timestamps;
    }
}
