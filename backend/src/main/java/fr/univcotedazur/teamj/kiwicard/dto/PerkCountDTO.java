package fr.univcotedazur.teamj.kiwicard.dto;

public class PerkCountDTO {
    private String perkType;
    private Long count;

    public PerkCountDTO(String perkType, Long count) {
        this.perkType = perkType;
        this.count = count;
    }

    public PerkCountDTO(Class<?> perkType, Long count) {
        this.perkType = perkType.getSimpleName();
        this.count = count;
    }


    public String getPerkType() {
        return perkType;
    }

    public void setPerkType(String perkType) {
        this.perkType = perkType;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
