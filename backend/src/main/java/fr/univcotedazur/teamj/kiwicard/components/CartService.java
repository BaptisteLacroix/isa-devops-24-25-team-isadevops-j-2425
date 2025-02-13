package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.dto.UsedPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.cart.ICartModifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICartModifier, ICartFinder, ICartCreator {


    @Override
    public CartDTO createCart(String customerEmail, List<ItemDTO> items) throws UnknownCustomerEmailException {
        return null;
    }

    @Override
    public Optional<CartDTO> findCustomerCart(String cartOwnerEmail) throws UnknownCustomerEmailException {
        return Optional.empty();
    }

    @Override
    public CartDTO updateCart(String cartOwnerEmail, CartDTO newCart, UsedPerkDTO usedPerk) throws UnknownCustomerEmailException {
        return null;
    }

    @Override
    public PurchaseDTO validateCart(String cartOwnerEmail) throws UnknownCustomerEmailException {
        return null;
    }
}
