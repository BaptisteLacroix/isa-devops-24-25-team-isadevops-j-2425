package fr.univcotedazur.teamj.kiwicard.mappers;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;

public class PerkMapper {
    private PerkMapper() {
    }
    public static IPerkDTO toDTO(AbstractPerk perk) {
        return perk.accept(new PerkToDTOVisitor());
    }
}
