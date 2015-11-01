package org.feup.cmov.inspectorticketapp.Models;


import lombok.Getter;

public class ErrorResponse {
    @Getter int statusCode;
    @Getter String error;
    @Getter String message;

    public String getMessage() {
        if (message == null) {
            return error;
        }
        return message;
    }
}
