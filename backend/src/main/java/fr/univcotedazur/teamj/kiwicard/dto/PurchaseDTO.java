package fr.univcotedazur.teamj.kiwicard.dto;

public record PurchaseDTO(String email, CartDTO cartDTO, PaymentDTO paymentDTO) { }
