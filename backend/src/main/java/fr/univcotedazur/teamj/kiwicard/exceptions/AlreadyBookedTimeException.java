package fr.univcotedazur.teamj.kiwicard.exceptions;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AlreadyBookedTimeException extends Exception {
    public AlreadyBookedTimeException(@NotNull String label, LocalDateTime startTime, int duration) {
        super("Une réservation est déjà enregistrée pour l'item " + label + " de " + startTime + " à " + startTime.plusHours(duration));
    }
}
