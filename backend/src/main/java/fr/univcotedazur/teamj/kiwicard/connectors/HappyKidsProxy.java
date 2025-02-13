package fr.univcotedazur.teamj.kiwicard.connectors;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IHappyKids;

import java.time.LocalDateTime;

public class HappyKidsProxy implements IHappyKids {
    @Override
    public HappyKidsDiscountDTO computeDiscount(LocalDateTime wantedSlot) throws ClosedTimeException {
        return null;
    }
}
