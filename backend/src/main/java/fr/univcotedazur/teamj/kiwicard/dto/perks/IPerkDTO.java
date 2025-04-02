package fr.univcotedazur.teamj.kiwicard.dto.perks;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.univcotedazur.teamj.kiwicard.mappers.PerkDTOVisitor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NPurchasedMGiftedPerkDTO.class, name = "NPurchasedMGiftedPerkDTO"),
        @JsonSubTypes.Type(value = TimedDiscountInPercentPerkDTO.class, name = "TimedDiscountInPercentPerkDTO"),
        @JsonSubTypes.Type(value = VfpDiscountInPercentPerkDTO.class, name = "VfpDiscountInPercentPerkDTO")
})
public interface IPerkDTO {
    Long perkId();
    @JsonGetter("description")
    default  String getDescription() {
        return this.toString();
    }

    <T> T accept(PerkDTOVisitor<T> visitor);
}
