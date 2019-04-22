package org.lsm.mobile.exception;

import android.support.annotation.NonNull;

/**
 * Signals that the course content returned from server is not valid. Example issues in the course's
 * content could be, it being not parsable or incomplete according to what we expect.
 */
public class CourseContentNotValidException extends Exception {
    public CourseContentNotValidException(@NonNull String message) {
        super(message);
    }

    public CourseContentNotValidException(String message, Exception e) {
        super(message, e);
    }
}
