package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPerkIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPerkManager;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = PerksController.BASE_URI)
public class PerksController {

    public static final String BASE_URI = "/perks";
    private final IPerkManager perksManager;
    private final IPerksConsumer perksConsumer;

    @Autowired
    public PerksController(IPerkManager perkManager, IPerksConsumer perksConsumer) {
        this.perksManager = perkManager;
        this.perksConsumer = perksConsumer;
    }

    @PostMapping
    public ResponseEntity<IPerkDTO> createPerk(@RequestBody IPerkDTO perkDTO) {
        IPerkDTO createdPerk = perksManager.createPerk(perkDTO);
        // On peut définir l’URI de la ressource créée si nécessaire
        return ResponseEntity.created(null).body(createdPerk);
    }

    @GetMapping("/{perkId}")
    public ResponseEntity<IPerkDTO> getPerkById(@PathVariable long perkId) throws UnknownPerkIdException {
        IPerkDTO perk = perksManager.findPerkById(perkId)
                .orElseThrow(() -> new UnknownPerkIdException(perkId));
        return ResponseEntity.ok(perk);
    }

    @GetMapping
    public ResponseEntity<List<IPerkDTO>> listAllPerks() {
        List<IPerkDTO> perks = perksManager.findAllPerks();
        return ResponseEntity.ok(perks);
    }

    @PatchMapping("/{perkId}")
    public ResponseEntity<Void> updatePerk(@PathVariable long perkId, @RequestBody IPerkDTO perkDTO)
            throws UnknownPerkIdException {
        perksManager.updatePerk(perkId, perkDTO);
        return ResponseEntity.noContent().build();
    }

    // Suppression d'un perk
    @DeleteMapping("/{perkId}")
    public ResponseEntity<Void> deletePerk(@PathVariable long perkId) throws UnknownPerkIdException {
        perksManager.deletePerk(perkId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint pour appliquer un perk (à adapter selon votre logique métier)
    @PostMapping("/{perkId}/apply")
    public ResponseEntity<String> applyPerk(@PathVariable long perkId, @RequestBody Object request)
            throws UnknownPerkIdException {
        // Ici, vous implémenterez la logique pour appliquer le perk (calcul de remise, vérification des conditions, etc.)
        // Pour l'instant, nous retournons une réponse simulée.
        String message = "Perk " + perkId + " appliqué avec les paramètres : " + request;
        return ResponseEntity.ok(message);
    }
}
