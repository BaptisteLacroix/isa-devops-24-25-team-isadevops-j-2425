package fr.univcotedazur.teamj.kiwicard.exceptions;

public class BookingTimeNotSetException extends Exception {
    public BookingTimeNotSetException() {
        super("Booking time is not set");
    }
}
