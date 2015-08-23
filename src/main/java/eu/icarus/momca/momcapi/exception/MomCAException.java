package eu.icarus.momca.momcapi.exception;

import org.jetbrains.annotations.NotNull;


/**
 * @author Daniel Jeller
 *         Created on 25.06.2015.
 */
public class MomcaException extends RuntimeException {

    /**
     * Instantiates a new MomCA-Exception from another exception.
     *
     * @param message The message
     * @param e       The original exception
     */
    public MomcaException(@NotNull String message, @NotNull Exception e) {
        super(message, e);
    }

    /**
     * Instantiates a new MomCa-Exception.
     *
     * @param message The message.
     */
    public MomcaException(@NotNull String message) {
        super(message);
    }
}
