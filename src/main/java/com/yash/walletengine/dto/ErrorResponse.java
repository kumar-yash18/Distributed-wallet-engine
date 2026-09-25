package com.yash.walletengine.dto;

import java.time.Instant;

public class ErrorResponse {
    private String code;
    private String message;
    private Instant timestamp;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
    }

    // getters (required for Jackson to serialize to JSON)
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
}