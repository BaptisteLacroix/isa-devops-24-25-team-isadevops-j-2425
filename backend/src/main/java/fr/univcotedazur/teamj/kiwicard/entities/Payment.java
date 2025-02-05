package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long paymentId;

    @NotBlank
    @Column
    private double amount;

    @NotBlank
    @Column
    LocalDateTime timestamp;
}


