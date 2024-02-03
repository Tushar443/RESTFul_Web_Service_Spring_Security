package com.example.demo.ws.ui.model.response;

import java.util.Date;

public class ErrorMessage {
    private Date timeStamp;
    private String message;

    public ErrorMessage(Date timeStamp, String message) {
        this.timeStamp = timeStamp;
        this.message = message;
    }

    public ErrorMessage() {
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
