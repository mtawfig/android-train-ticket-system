package org.feup.cmov.inspectorticketapp.Models;

import lombok.Getter;

public class HttpResponse {
    @Getter boolean isError;
    @Getter String content;

    public HttpResponse(boolean isError, String content) {
        this.isError = isError;
        this.content = content;
    }
}