package fr.univcotedazur.teamj.kiwicard.interfaces;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;

/**
 * Vérification de la disponibilité d'une réduction chez HappyKids
 */
public interface IHappyKids {
    HappyKidsDiscountDTO computeDiscount(CartItem item, double discountRate) throws ClosedTimeException, UnreachableExternalServiceException;
}
