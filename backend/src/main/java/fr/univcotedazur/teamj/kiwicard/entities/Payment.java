package fr.univcotedazur.teamj.kiwicard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long paymentId;

    @NotNull
    @Column
    private double amount;

    @NotNull
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

    @NotNull
    public double getAmount() {
        return amount;
    }

    public void setAmount(@NotNull double amount) {
        this.amount = amount;
    }

    public @NotNull LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NotNull LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}


