package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class CartItem {

    @Id
    @GeneratedValue
    private Long cartItemId;

    @Column
    private int quantity;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

}

