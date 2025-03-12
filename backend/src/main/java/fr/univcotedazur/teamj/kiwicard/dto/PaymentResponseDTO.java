package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;

import java.util.List;

public record PaymentResponseDTO(double totalPrice, List<IPerkDTO> successfullyAppliedPerks) {
}

