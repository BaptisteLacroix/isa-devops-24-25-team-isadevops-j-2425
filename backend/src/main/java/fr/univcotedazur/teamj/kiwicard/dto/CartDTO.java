package fr.univcotedazur.teamj.kiwicard.dto;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The DTO representing a cart
 * @param cartId The cart id
 * @param partner The partner associated with the cart
 * @param items The items in the cart
 * @param perksList The perks added to the cart
 */
public record CartDTO (long cartId, PartnerDTO partner, Set<CartItemDTO> items, List<IPerkDTO> perksList) {
    public CartDTO(Cart entity) {
        this(
                entity.getCartId(),
                new PartnerDTO(entity.getPartner()),
                entity.getItems().stream().map(CartItemDTO::new).collect(Collectors.toSet()),
                entity.getPerksToUse().stream().map(PerkMapper::toDTO).toList()
        );
    }
}
