package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.components.PerksService;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.NoCartException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPerkManager;
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

    @GetMapping("/{perkId}")
    public ResponseEntity<IPerkDTO> getPerkById(@PathVariable long perkId) throws UnknownPerkIdException {
        IPerkDTO perk = perksManager.findPerkById(perkId);
        return ResponseEntity.ok(perk);
    }

    @GetMapping
    public ResponseEntity<List<IPerkDTO>> listAllPerks() {
        List<IPerkDTO> perks = perksManager.findAllPerks();
        return ResponseEntity.ok(perks);
    }

    public record ApplyPerkRequest(String emailCustomer) {}

    @PostMapping("/{perkId}/apply")
    public ResponseEntity<String> applyPerk(@PathVariable long perkId, @RequestBody ApplyPerkRequest payload)
            throws UnknownPerkIdException, UnknownCustomerEmailException {
        return ResponseEntity.ok(String.valueOf(perksService.applyPerk(perkId, payload.emailCustomer())));
    }

    @GetMapping("/consumable")
    public ResponseEntity<List<IPerkDTO>> findConsumablePerksForConsumerAtPartner(@RequestParam String consumerEmail)
            throws UnknownCustomerEmailException, NoCartException {
        List<IPerkDTO> consumablePerks = perksService.findConsumablePerksForConsumerAtPartner(consumerEmail);
        return ResponseEntity.ok(consumablePerks);
    }
}
