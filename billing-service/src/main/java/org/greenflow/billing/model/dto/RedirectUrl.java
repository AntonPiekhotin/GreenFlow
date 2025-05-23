package org.greenflow.billing.model.dto;

public record RedirectUrl(String url) {
    public RedirectUrl {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank");
        }
    }
}
