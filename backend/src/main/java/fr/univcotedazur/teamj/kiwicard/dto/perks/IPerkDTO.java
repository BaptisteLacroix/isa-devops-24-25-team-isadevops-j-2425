package fr.univcotedazur.teamj.kiwicard.dto.perks;

import com.fasterxml.jackson.annotation.JsonGetter;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

public interface IPerkDTO {
    Long perkId();
    @JsonGetter("description")
    default  String getDescription() {
        return this.toString();
    }

    <T> T accept(PerkDTOVisitor<T> visitor);
}
