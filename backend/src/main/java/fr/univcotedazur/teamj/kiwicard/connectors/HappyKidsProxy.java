package fr.univcotedazur.teamj.kiwicard.connectors;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.interfaces.HappyKids;

import java.time.LocalDateTime;

public class HappyKidsProxy implements HappyKids {
    @Override
    public HappyKidsDiscountDTO computeDiscount(LocalDateTime wantedSlot) throws ClosedTimeException {
        return null;
    }
}
