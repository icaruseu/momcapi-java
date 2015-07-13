package eu.icarus.momca.momcapi.xml;

import org.jetbrains.annotations.NotNull;

/**
 * A validation problem in an XML resource.
 *
 * @author Daniel Jeller
 *         Created on 10.07.2015.
 */
public class XmlValidationProblem {

    private final int column;
    @NotNull
    private final Level level;
    private final int line;
    @NotNull
    private final String message;

    /**
     * The enum Level.
     */
    public enum Level {
        ERROR,
        FATAL_ERROR,
        WARNING
    }

    public XmlValidationProblem(@NotNull Level level, int line, int column, @NotNull String message) {
        this.level = level;
        this.line = line;
        this.column = column;
        this.message = message;
    }

    /**
     * @return The column the problem appeared in.
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return The problem level.
     */
    @NotNull
    public Level getLevel() {
        return level;
    }

    /**
     * @return The line the problem appeard in.
     */
    public int getLine() {
        return line;
    }

    /**
     * @return The problem description by the validator.
     */
    @NotNull
    public String getMessage() {
        return message;
    }
}
