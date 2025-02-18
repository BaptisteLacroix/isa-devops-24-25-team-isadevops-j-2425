package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;

public record PerkDTO (long perkId, String description) {

    public PerkDTO(AbstractPerk abstractPerk) {
        this(abstractPerk.getPerkId(), abstractPerk.getDescription());
    }
}
