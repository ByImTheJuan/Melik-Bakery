package com.hyd.pipes_bakery_backend.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WompiTransactionDTO {

    private String id;
    private String reference;
    private String status;

    @JsonProperty("amount_in_cents")
    private Long amountInCents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }
}
