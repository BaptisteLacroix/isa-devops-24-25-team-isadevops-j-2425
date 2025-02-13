package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = PartnerController.PARTNER_URI)
public class PartnerController {

    public static final String PARTNER_URI = "/partner";

    private final IPartnerManager partnerManager;

    @Autowired
    public PartnerController(IPartnerManager partnerManager) {
        this.partnerManager = partnerManager;
    }
}
