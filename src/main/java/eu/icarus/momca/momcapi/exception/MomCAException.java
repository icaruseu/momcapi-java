package eu.icarus.momca.momcapi.exception;

import org.jetbrains.annotations.NotNull;


/**
 * A MomCA-exception.
 *
 * @author Daniel Jeller
 *         Created on 25.06.2015.
 */
public class MomcaException extends RuntimeException {

    /**
     * Instantiates a new MomCA-exception from another exception.
     *
     * @param message the message
     * @param e       the original exception
     */
    public MomcaException(@NotNull String message, @NotNull Exception e) {
        super(message, e);
    }

}
