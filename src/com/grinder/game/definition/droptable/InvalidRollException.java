package com.grinder.game.definition.droptable;

public class InvalidRollException extends RuntimeException {

    public InvalidRollException() {
    }

    public InvalidRollException(String message) {
        super(message);
    }

    public InvalidRollException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRollException(Throwable cause) {
        super(cause);
    }

    public InvalidRollException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
