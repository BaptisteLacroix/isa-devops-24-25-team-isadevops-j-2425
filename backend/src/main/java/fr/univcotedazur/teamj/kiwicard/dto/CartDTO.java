package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CartDTO (long cartId, PartnerDTO partner, Set<CartItemDTO> items, List<AbstractPerk> perksList) {
    public CartDTO(Cart entity) {
        this(
                entity.getCartId(),
                new PartnerDTO(entity.getPartner()),
                entity.getItemList().stream().map(CartItemDTO::new).collect(Collectors.toSet()),
                entity.getPerksToUse()
        );
    }
}
