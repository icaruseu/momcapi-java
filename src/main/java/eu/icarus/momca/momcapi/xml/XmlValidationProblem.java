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
    private final int line;
    @NotNull
    private final String message;
    @NotNull
    private final SeverityLevel severityLevel;

    /**
     * The enum SeverityLevel.
     */
    public enum SeverityLevel {
        WARNING,
        ERROR,
        FATAL_ERROR
    }

    /**
     * Instantiates a new XmlValidationProblem.
     *
     * @param severityLevel The severity level.
     * @param line          The line the problem appeared on.
     * @param column        The column the problem appeared on.
     * @param message       The message by the validator, indicating the problem.
     */
    public XmlValidationProblem(@NotNull SeverityLevel severityLevel, int line, int column, @NotNull String message) {
        this.severityLevel = severityLevel;
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

    /**
     * @return The problem severityLevel.
     */
    @NotNull
    public SeverityLevel getSeverityLevel() {
        return severityLevel;
    }
}
