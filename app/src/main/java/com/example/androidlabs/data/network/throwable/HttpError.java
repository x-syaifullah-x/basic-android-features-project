package com.example.androidlabs.data.network.throwable;

import androidx.annotation.Nullable;

public class HttpError extends Throwable {
    public HttpError(@Nullable String message) {
        super(message);
    }
}
