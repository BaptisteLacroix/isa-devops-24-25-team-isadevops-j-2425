package fr.univcotedazur.teamj.kiwicard.exceptions;


import java.time.Duration;

public class ForbiddenDurationException extends Exception {
    public ForbiddenDurationException(Duration duration) {
        super("Duration is expected to be less than a day, got " + duration);
    }
}
