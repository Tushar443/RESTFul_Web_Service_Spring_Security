package com.example.demo.ws.ui.model.response;

public enum ErrorMessages {
    MESSING_REQUIRED_FIELDS("Missing required field . check documentation");

    private String errorMessage;

    ErrorMessages() {
    }

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
