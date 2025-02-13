package fr.univcotedazur.teamj.kiwicard.interfaces.partner;


import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;

import java.util.List;

public interface IItemManager {
    Item createItem(Item ItemToCreate);
    Item findItemById(long itemId) throws UnknownItemIdException;
    List<Item> findAllItem();
    void udpateItem(long itemId, Item newItem) throws UnknownItemIdException;
    void deleteItem(long itemId) throws UnknownItemIdException;
}
