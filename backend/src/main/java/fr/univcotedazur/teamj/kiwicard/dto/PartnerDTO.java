package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Partner;

public record PartnerDTO(String name, String address) {
    public PartnerDTO(Partner entity) {
       this(entity.getName(), entity.getAddress());
    }
}
