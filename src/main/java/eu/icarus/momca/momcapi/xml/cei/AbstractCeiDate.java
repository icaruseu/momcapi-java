package eu.icarus.momca.momcapi.xml.cei;

import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an abstract <a href="http://www.cei.lmu.de/index.php">CEI</a> date that is an XML element with a
 * literal value, e.g. {@code 2nd December 1678} and specifies methods for concrete date classes to implement.
 *
 * @author Daniel Jeller
 *         Created on 10.07.2015.
 */
public abstract class AbstractCeiDate extends Element {

    private final String literalDate;

    /**
     * Instantiates a new abstract cei-date.
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
     * @return {@code True} if the date could be converted to the other date type.<br/>
     * </br>
     * Examples:
     * <ul>
     * <li>{@code <cei:date value="17859999">1785</cei:date>} could be
     * {@code <cei:dateRange from="17850101" to="17851231">1785</cei:dateRange>}</li>
     * <li>{@code <cei:dateRange from="17850712" to="17850712">12th July 1785</cei:dateRange>} could be
     * {@code <cei:date value="17850712">12th July 1785</cei:date>}</li>
     * </ul>
     */
    public abstract boolean couldBeOtherDateType();

    /**
     * @return The literal date.
     */
    public String getLiteralDate() {
        return literalDate;
    }

    /**
     * @return {@code True} if the date is a valid {@code cei:date}.
     */
    public abstract boolean isValid();

    @Override
    public String toString() {
        return "AbstractCeiDate{" +
                "literalDate='" + literalDate + '\'' +
                "} " + super.toString();
    }

}
