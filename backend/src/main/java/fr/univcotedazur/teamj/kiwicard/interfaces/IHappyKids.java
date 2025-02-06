package fr.univcotedazur.teamj.kiwicard.interfaces;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;

import java.time.LocalDateTime;

/**
 * Vérification de la disponibilité d'une réduction chez HappyKids
 */
public interface IHappyKids {
    HappyKidsDiscountDTO computeDiscount(LocalDateTime wantedSlot) throws ClosedTimeException;
}
