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

    public Payment() {
    }

    public Payment(double amount, LocalDateTime timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    @NotBlank
    public double getAmount() {
        return amount;
    }

    public void setAmount(@NotBlank double amount) {
        this.amount = amount;
    }

    public @NotBlank LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NotBlank LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}


