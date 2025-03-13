package fr.univcotedazur.teamj.kiwicard.connectors.externaldto;

// External DTO (Data Transfert Object) to POST payment request to the external Bank system
public record PaymentRequestDTO (String cardNumber, double amount)
{
}
