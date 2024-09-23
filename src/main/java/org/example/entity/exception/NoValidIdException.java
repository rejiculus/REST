package org.example.entity.exception;

/**
 * Thrown when id is not valid.
 */
public class NoValidIdException extends RuntimeException {
    /**
     * Thrown when trying to call getId method, but id si not specified.
     */
    public NoValidIdException() {
        super("Id is not specified!");
    }

    /**
     * Thrown when trying to specify wrong id.
     *
     * @param id specified id.
     */
    public NoValidIdException(Long id) {
        super(String.format("ID can't be less than zero! Current value is '%d'.", id));
    }

}
