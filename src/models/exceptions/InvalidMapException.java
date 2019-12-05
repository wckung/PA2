package models.exceptions;

import org.jetbrains.annotations.NonNls;

/**
 * Exception indicating that the map is invalid for any reason.
 */
public class InvalidMapException extends RuntimeException {

    public InvalidMapException(@NonNls final String message) {
        super(message);
    }

    public InvalidMapException(Throwable cause) {
        super(cause);
    }
}
