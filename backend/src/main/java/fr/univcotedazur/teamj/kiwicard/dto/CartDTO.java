package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CartDTO (long cartId, PartnerDTO partner, Set<CartItemDTO> items, List<IPerkDTO> perksList) {
    public CartDTO(Cart entity) {
        this(entity.getCartId(), new PartnerDTO(entity.getPartner()), entity.getItemList().stream().map(CartItemDTO::new).collect(Collectors.toSet()), entity.getPerksList().stream().map(IPerkDTO::toDTO).collect(Collectors.toList()));
    }
}
