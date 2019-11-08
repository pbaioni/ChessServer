package app.persistence.exceptions;

public class EntityIdMismatchException extends RuntimeException {

    public EntityIdMismatchException() {
        super();
    }

    public EntityIdMismatchException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EntityIdMismatchException(final String message) {
        super(message);
    }

    public EntityIdMismatchException(final Throwable cause) {
        super(cause);
    }
}
