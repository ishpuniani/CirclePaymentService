package com.circle.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.UUID;

public class Transaction {
    private UUID id;

    private UUID sender_id;

    private UUID receiver_id;

    private double amount;

    private Status status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date created_at;

    public Transaction() {
        this.id = UUID.randomUUID();
        this.created_at = new Date();
        this.status = Status.PENDING;
    }

    public Transaction(UUID sender_id, UUID receiver_id, double amount) {
        this.id = UUID.randomUUID();
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.amount = amount;
        this.status = Status.PENDING;
        this.created_at = new Date();
    }

    public Transaction(UUID id, UUID sender_id, UUID receiver_id, double amount, Status status, Date created_at) {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.amount = amount;
        this.status = status;
        this.created_at = created_at;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSender_id() {
        return sender_id;
    }

    public void setSender_id(UUID sender_id) {
        this.sender_id = sender_id;
    }

    public UUID getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(UUID receiver_id) {
        this.receiver_id = receiver_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    /*  public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSenderId() {
        return sender_id;
    }

    public void setSenderId(UUID sender_id) {
        this.sender_id = sender_id;
    }

    public UUID getReceiverId() {
        return receiver_id;
    }

    public void setReceiverId(UUID receiver_id) {
        this.receiver_id = receiver_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }*/

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender_id=" + sender_id +
                ", receiver_id=" + receiver_id +
                ", amount=" + amount +
                ", status=" + status +
                ", created_at=" + created_at +
                '}';
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Status {
        PENDING(0),
        DONE(1),
        FAILED(2);

        private final int value;

        private Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
