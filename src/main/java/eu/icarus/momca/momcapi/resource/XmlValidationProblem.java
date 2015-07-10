package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 10.07.2015.
 */
public class XmlValidationProblem {

    private final int column;
    @NotNull
    private final Level level;
    private final int line;
    @NotNull
    private final String message;

    public enum Level {ERROR, FATAL_ERROR, WARNING}

    public XmlValidationProblem(@NotNull Level level, int line, int column, @NotNull String message) {
        this.level = level;
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public int getColumn() {
        return column;
    }

    @NotNull
    public Level getLevel() {
        return level;
    }

    public int getLine() {
        return line;
    }

    @NotNull
    public String getMessage() {
        return message;
    }
}
