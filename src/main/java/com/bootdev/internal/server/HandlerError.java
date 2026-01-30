package com.bootdev.internal.server;

import com.bootdev.internal.response.StatusCode;

public record HandlerError(String message, StatusCode statusCode) {
}
