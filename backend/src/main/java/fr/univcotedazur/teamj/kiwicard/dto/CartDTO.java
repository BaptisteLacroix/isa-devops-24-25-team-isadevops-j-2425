package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CartDTO (long cartId, PartnerDTO partner, Set<CartItemDTO> items, List<IPerkDTO> perksList) {
    public CartDTO(Cart entity) {
        this(
                entity.getCartId(),
                new PartnerDTO(entity.getPartner()),
                entity.getItems().stream().map(CartItemDTO::new).collect(Collectors.toSet()),
                entity.getPerksUsed().stream().map(PerkMapper::toDTO).toList()
        );
    }
}
