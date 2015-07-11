package eu.icarus.momca.momcapi.xml.cei;

import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an abstract <a href="http://www.cei.lmu.de/index.php">CEI</a> date that is an XML element with a literal value, e.g. {@code 2nd December 1678} and specifies methods for concrete date classes to implement.
 *
 * @author Daniel Jeller
 *         Created on 10.07.2015.
 */
public abstract class AbstractCeiDate extends Element {

    private final String literalDate;

    /**
     * Instantiates a new Abstract cei date.
     *
     * @param element     The root element, e.g. {@code cei:date} or {@code cei:dateRange}.
     * @param literalDate The literal date, e.g. {@code 2nd December 1678}.
     */
    AbstractCeiDate(@NotNull Element element, @NotNull String literalDate) {
        super(element);
        this.literalDate = literalDate;
        this.appendChild(literalDate);
    }

    /**
     * @return The literal date.
     */
    public String getLiteralDate() {
        return literalDate;
    }

    /**
     * @return {@code True}, if the charters is a valid CEI date.
     */
    public abstract boolean isValid();

    /**
     * Is wrong date type.
     *
     * @return the boolean
     */
    public abstract boolean isWrongDateType();

    @Override
    public String toString() {
        return "AbstractCeiDate{" +
                "literalDate='" + literalDate + '\'' +
                "} " + super.toString();
    }

}
