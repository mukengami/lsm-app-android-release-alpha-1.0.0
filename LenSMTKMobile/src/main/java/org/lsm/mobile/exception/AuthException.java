package org.lsm.mobile.exception;

import android.support.annotation.NonNull;

public class AuthException extends Exception {
    public AuthException(@NonNull String message) {
        super(message);
    }
}
