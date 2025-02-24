package fr.univcotedazur.teamj.kiwicard.dto;

import java.time.LocalDateTime;

public class PaymentDTO {
    private Long paymentId;
    private double amount;
    private LocalDateTime timestamp;

    public PaymentDTO() {
    }

    public PaymentDTO(Long paymentId, double amount, LocalDateTime timestamp) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
