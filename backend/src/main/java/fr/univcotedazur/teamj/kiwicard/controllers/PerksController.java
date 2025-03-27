package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.components.PerksService;
import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.InapplicablePerkException;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = PerksController.BASE_URI)
public class PerksController {

    public static final String BASE_URI = "/perks";
    private final IPerkManager perksManager;
    private final PerksService perksService;

    @Autowired
    public PerksController(IPerkManager perkManager, PerksService perksService) {
        this.perksManager = perkManager;
        this.perksService = perksService;
    }

    /**
     * Obtenir un perk par son id à partir de l'URI /perks/{perkId}
     *
     * @param perkId l'id du perk à obtenir
     * @return le perk correspondant à l'id
     * @throws UnknownPerkIdException si le perk n'existe pas
     */
    @GetMapping("/{perkId}")
    public ResponseEntity<IPerkDTO> getPerkById(@PathVariable long perkId) throws UnknownPerkIdException {
        IPerkDTO perk = perksManager.findPerkById(perkId);
        return ResponseEntity.ok(perk);
    }

    /**
     * Obtenir la liste de tous les perks à partir de l'URI /perks
     *
     * @return la liste de tous les perks
     */
    @GetMapping
    public ResponseEntity<List<IPerkDTO>> listAllPerks() {
        List<IPerkDTO> perks = perksManager.findAllPerks();
        return ResponseEntity.ok(perks);
    }

    public record ApplyPerkRequest(String emailCustomer) {
    }

    /**
     * Appliquer un perk à un client à partir de l'URI /perks/{perkId}/apply
     * @param perkId l'id du perk à appliquer
     * @param payload l'email du client
     * @return true si le perk a été appliqué, false sinon
     * @throws UnknownPerkIdException si le perk n'existe pas
     * @throws UnknownCustomerEmailException si le client n'existe pas
     */
    @PostMapping("/{perkId}/apply")
    public ResponseEntity<CartDTO> applyPerk(@PathVariable long perkId, @RequestBody ApplyPerkRequest payload)
            throws UnknownPerkIdException, UnknownCustomerEmailException, NoCartException, InapplicablePerkException {
        return ResponseEntity.ok(perksService.addPerkToApply(perkId, payload.emailCustomer()));
    }

    /**
     * Obtenir la liste des perks consommables pour un client chez un partenaire à partir de l'URI /perks/consumable
     * @param consumerEmail l'email du client
     * @return la liste des perks consommables pour le client chez le partenaire
     * @throws UnknownCustomerEmailException si le client n'existe pas
     * @throws NoCartException si le client n'a pas de panier
     */
    @GetMapping("/consumable")
    public ResponseEntity<List<IPerkDTO>> findConsumablePerksForConsumerAtPartner(@RequestParam String consumerEmail)
            throws UnknownCustomerEmailException, NoCartException {
        List<IPerkDTO> consumablePerks = perksService.findConsumablePerksForConsumerAtPartner(consumerEmail);
        return ResponseEntity.ok(consumablePerks);
    }
}
