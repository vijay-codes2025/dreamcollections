package com.dreamcollections.services.identity.payload.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpResponse {
    private boolean success;
    private String message;
    private String maskedPhone;
    private String maskedEmail;
    private long expiresInSeconds;

    public OtpResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
