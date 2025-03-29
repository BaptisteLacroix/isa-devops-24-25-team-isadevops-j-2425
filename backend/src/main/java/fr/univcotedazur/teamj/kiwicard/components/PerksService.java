package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksConsumer;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksFinder;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerksService implements IPerksConsumer {
    private final IPerksFinder perksFinder;
    private final ICustomerFinder customerFinder;

    public PerksService(IPerksFinder perksRepository, ICustomerFinder customerFinder) {
        this.perksFinder = perksRepository;
        this.customerFinder = customerFinder;
    }

    /**
     * Ajoute un perks à la liste des perks à utiliser pour le client
     *
     * @param perkId         l'id du perk à ajouter
     * @param cartOwnerEmail l'email du client
     * @return true si le perk a été ajouté, false sinon
     * @throws UnknownPerkIdException        si le perk n'existe pas
     * @throws UnknownCustomerEmailException si le client n'existe pas
     */
    @Override
    @Transactional
    public boolean addPerkToApply(long perkId, String cartOwnerEmail) throws UnknownPerkIdException, UnknownCustomerEmailException, NoCartException {
        AbstractPerk perk = PerkMapper.fromDTO(perksFinder.findPerkById(perkId));
        Customer customer = customerFinder.findCustomerByEmail(cartOwnerEmail);

        Cart cart = customer.getCart();
        if (cart == null) {
            throw new NoCartException(customer.getEmail());
        }
        if (cart.getPartner().getPerkSet().stream().map(AbstractPerk::getPerkId).noneMatch(id -> id.equals(perkId))) {
            throw new UnknownPerkIdException(perkId);
        }
        if (perk.isConsumableFor(customer)) {
            cart.addPerkToUse(perk);
            return true;
        }
        return false;
    }

    /**
     * Cherche les perks consommables pour un client chez un partenaire
     *
     * @param consumerEmail l'email du client
     * @return la liste des perks consommables pour le client chez le partenaire
     * @throws UnknownCustomerEmailException si le client n'existe pas
     * @throws NoCartException               si le client n'a pas de panier
     */
    @Override
    @Transactional
    public List<IPerkDTO> findConsumablePerksForConsumerAtPartner(String consumerEmail) throws UnknownCustomerEmailException, NoCartException {
        Customer customer = customerFinder.findCustomerByEmail(consumerEmail);
        Cart cart = customer.getCart();
        if (cart == null) {
            throw new NoCartException(customer.getEmail());
        }
        Partner partner = cart.getPartner();
        return partner.getPerkSet()
                .stream()
                .filter(perk -> perk.isConsumableFor(customer))
                .map(PerkMapper::toDTO)
                .toList();
    }
}
