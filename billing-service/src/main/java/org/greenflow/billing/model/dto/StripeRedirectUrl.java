package org.greenflow.billing.model.dto;

public record StripeRedirectUrl(String url) {
    public StripeRedirectUrl {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank");
        }
    }
}
