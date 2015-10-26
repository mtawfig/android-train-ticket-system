package org.feup.cmov.inspectorticketapp.Models;


import lombok.Getter;

public class ErrorResponse {
    @Getter int statusCode;
    @Getter String error;
    @Getter String message;
}
