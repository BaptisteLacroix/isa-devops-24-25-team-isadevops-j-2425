package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.ItemCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PartnerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownItemIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.univcotedazur.teamj.kiwicard.controllers.PartnerController.BASE_URI;

@RestController
@RequestMapping(path = BASE_URI)
public class PartnerController {

    public static final String BASE_URI = "/partners";

    private final IPartnerManager partnerManager;

    @Autowired
    public PartnerController(IPartnerManager partnerManager) {
        this.partnerManager = partnerManager;
    }

    @PostMapping
    public ResponseEntity<PartnerDTO> createPartner(@RequestBody PartnerCreationDTO partnerCreationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(partnerManager.createPartner(partnerCreationDTO));
    }

    @GetMapping("/{partnerId}")
    public ResponseEntity<PartnerDTO> getPartnerById(@PathVariable long partnerId) throws UnknownPartnerIdException {
        return ResponseEntity.ok()
                .body(new PartnerDTO(partnerManager.findPartnerById(partnerId)));
    }

    @GetMapping
    public ResponseEntity<List<PartnerDTO>> listAllPartners() {
        return ResponseEntity.ok()
                .body(partnerManager.findAllPartner());
    }

    @PatchMapping("/{partnerId}/add-item")
    public ResponseEntity<Void> addItemToPartnerCatalog(@PathVariable long partnerId, @RequestBody ItemCreationDTO itemDTO) throws UnknownPartnerIdException {
        partnerManager.addItemToPartnerCatalog(partnerId, itemDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{partnerId}/remove-item/{itemId}")
    public ResponseEntity<Boolean> removeItemFromPartnerCatalog(@PathVariable long partnerId, @PathVariable long itemId) throws UnknownPartnerIdException, UnknownItemIdException {
        partnerManager.removeItemFromPartnerCatalog(partnerId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{partnerId}/items")
    public ResponseEntity<List<ItemDTO>> listAllItemsFromPartner(@PathVariable long partnerId) throws UnknownPartnerIdException {
        return ResponseEntity.ok()
                .body(
                        partnerManager.findAllPartnerItems(partnerId).stream()
                                .map(ItemDTO::new)
                                .toList()
                );
    }

    @GetMapping("/{partnerId}/perks")
    public ResponseEntity<List<IPerkDTO>> listAllPerksFromPartner(@PathVariable long partnerId) throws UnknownPartnerIdException {
        return ResponseEntity.ok()
                .body(partnerManager.findAllPartnerPerks(partnerId));
    }
}
