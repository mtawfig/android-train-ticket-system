package org.feup.cmov.userticketapp.Models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class HttpResponse {
    @Getter boolean isError;
    @Getter String content;

    public HttpResponse(boolean isError, String content) {
        this.isError = isError;
        this.content = content;
    }
}