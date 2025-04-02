package fr.univcotedazur.teamj.kiwicard.dto;

public record PerkCountDTO(String perkType, Long count) {
    public PerkCountDTO(Class<?> perkType, Long count) {
        this(perkType.getSimpleName(),count);
    }
}
