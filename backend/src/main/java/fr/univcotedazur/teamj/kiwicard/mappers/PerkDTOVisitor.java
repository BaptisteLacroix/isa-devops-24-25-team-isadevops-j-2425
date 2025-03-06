package fr.univcotedazur.teamj.kiwicard.mappers;

import fr.univcotedazur.teamj.kiwicard.dto.perks.NPurchasedMGiftedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.TimedDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.VfpDiscountInPercentPerkDTO;

public interface PerkDTOVisitor<T> {
    T fromDTO(NPurchasedMGiftedPerkDTO dto);
    T fromDTO(TimedDiscountInPercentPerkDTO dto);
    T fromDTO(VfpDiscountInPercentPerkDTO dto);
}
