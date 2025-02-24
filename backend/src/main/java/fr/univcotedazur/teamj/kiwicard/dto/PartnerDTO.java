package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Partner;

public record PartnerDTO(long id, String name, String address) {
    public PartnerDTO(Partner entity) {
       this(entity.getPartnerId(), entity.getName(), entity.getAddress());
    }
}
