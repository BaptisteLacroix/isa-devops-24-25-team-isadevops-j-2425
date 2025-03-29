package fr.univcotedazur.teamj.kiwicard.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.univcotedazur.teamj.kiwicard.dto.ItemCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Item {

    @Id
    @GeneratedValue
    private Long itemId;

    @NotNull
    @Column
    private String label;

    @NotNull
    @Column
    private double price;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    public Item() {
    }

    public Item(@NotNull String label, @NotNull double price) {
        this.label = label;
        this.price = price;
    }

    public Item(ItemDTO itemDTO) {
        this(itemDTO.label(), itemDTO.price());
        this.itemId = itemDTO.itemId();
    }

    public Item(ItemCreationDTO itemDTO) {
        this(itemDTO.label(), itemDTO.price());
    }

    /**
     * package private constructor for testing purposes
     * @param id l'id de l'item
     * @param label le label de l'item
     * @param price le prix de l'item
     */
    private Item(int id, String label, double price) {
        this.itemId = (long) id;
        this.label = label;
        this.price = price;
    }

    public static Item createTestItem(int id, String label, double price) {
        return new Item(id, label, price);
    }

    public Long getItemId() {
        return itemId;
    }

    public @NotNull String getLabel() {
        return label;
    }

    @NotNull
    public double getPrice() {
        return price;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @JsonIgnore
    public Partner getPartner() {
        return partner;
    }
}
