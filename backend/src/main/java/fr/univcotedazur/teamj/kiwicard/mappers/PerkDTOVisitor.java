package fr.univcotedazur.teamj.kiwicard.mappers;

import fr.univcotedazur.teamj.kiwicard.dto.perks.NPurchasedMGiftedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.TimedDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.VfpDiscountInPercentPerkDTO;

public interface PerkDTOVisitor<T> {
    T visit(NPurchasedMGiftedPerkDTO dto);
    T visit(TimedDiscountInPercentPerkDTO dto);
    T visit(VfpDiscountInPercentPerkDTO dto);
}
