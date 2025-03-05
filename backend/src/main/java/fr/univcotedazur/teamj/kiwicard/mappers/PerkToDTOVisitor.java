package fr.univcotedazur.teamj.kiwicard.mappers;

import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.NPurchasedMGiftedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.TimedDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.VfpDiscountInPercentPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.NPurchasedMGiftedPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;

public class PerkToDTOVisitor implements PerkVisitor<IPerkDTO> {

    @Override
    public IPerkDTO visit(NPurchasedMGiftedPerk perk) {
        if (perk.getItem() == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        return new NPurchasedMGiftedPerkDTO(
                perk.getPerkId(),
                perk.getNbPurchased(),
                new ItemDTO(perk.getItem()),
                perk.getNbGifted()
        );
    }

    @Override
    public IPerkDTO visit(TimedDiscountInPercentPerk perk) {
        return new TimedDiscountInPercentPerkDTO(
                perk.getPerkId(),
                perk.getTime(),
                perk.getDiscountRate()
        );
    }

    @Override
    public IPerkDTO visit(VfpDiscountInPercentPerk perk) {
        return new VfpDiscountInPercentPerkDTO(
                perk.getPerkId(),
                perk.getDiscountRate()
        );
    }
}
