package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CartCreationDTO(PartnerDTO partner, Set<CartItemDTO> items, List<IPerkDTO> perksList) {
    public CartCreationDTO(Partner partner, Set<CartItem> items, List<AbstractPerk> perksList) {
        this(new PartnerDTO(partner), items.stream().map(CartItemDTO::new).collect(Collectors.toSet()), perksList.stream().map(IPerkDTO::toDTO).collect(Collectors.toList()));
    }
}
