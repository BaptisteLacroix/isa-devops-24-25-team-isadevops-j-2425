package fr.univcotedazur.teamj.kiwicard.mappers;

import fr.univcotedazur.teamj.kiwicard.dto.perks.NPurchasedMGiftedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.TimedDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.VfpDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.*;

public class PerkFromDTOVisitor implements PerkDTOVisitor<AbstractPerk> {

    @Override
    public AbstractPerk visit(NPurchasedMGiftedPerkDTO dto) {
        return new NPurchasedMGiftedPerk(dto);
    }

    @Override
    public AbstractPerk visit(TimedDiscountInPercentPerkDTO dto) {
        return new TimedDiscountInPercentPerk(dto);
    }

    @Override
    public AbstractPerk visit(VfpDiscountInPercentPerkDTO dto) {
        return new VfpDiscountInPercentPerk(dto);
    }
}

