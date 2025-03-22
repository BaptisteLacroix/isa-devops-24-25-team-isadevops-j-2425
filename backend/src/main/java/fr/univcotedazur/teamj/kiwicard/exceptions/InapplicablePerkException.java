package fr.univcotedazur.teamj.kiwicard.exceptions;

import fr.univcotedazur.teamj.kiwicard.dto.perks.IPerkDTO;
import jakarta.validation.constraints.NotNull;

public class InapplicablePerkException extends Exception {
    public InapplicablePerkException(@NotNull IPerkDTO perk) {
        super("Impossible d'appliquer le perk " + perk + " à ce panier");
    }
}
