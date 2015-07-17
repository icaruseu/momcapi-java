package eu.icarus.momca.momcapi.xml.cei;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@code cei:dateRange} date as specified in the
 * <a href="http://www.cei.lmu.de/element.php?ID=221">CEI specification</a>. It consists of a literal date as well as
 * numeric to and from date values.<br/>
 * <br/>
 * Example in XML-form:<br/>
 * {@code <cei:dateRange from="12970301" to="12970331">March 1297</cei:date>}
 *
 * @author Daniel Jeller
 *         Created on 10.07.2015.
 */
public class CeiDateRange extends AbstractCeiDate {

    @NotNull
    private final NumericDate numericFromDate;
    @NotNull
    private final NumericDate numericToDate;


    /**
     * Instantiates a new ceiDateRange.
     *
     * @param numericFromDate The numeric {@code from} date value, e.q. {@code 12970301}.
     * @param numericToDate   The numeric {@code to} date value, e.q. {@code 12970331}.
     * @param literalDate     The literal date value, e.g. {@code March 1297}.
     */
    public CeiDateRange(@NotNull String numericFromDate, @NotNull String numericToDate, @NotNull String literalDate) {

        super(new Element("cei:dateRange", Namespace.CEI.getUri()), literalDate);

        addAttribute(new Attribute("from", numericFromDate));
        addAttribute(new Attribute("to", numericToDate));

        this.numericFromDate = new NumericDate(numericFromDate);
        this.numericToDate = new NumericDate(numericToDate);

    }

    @Override
    public boolean couldBeOtherDateType() {
        return numericFromDate.equals(numericToDate);
    }

    /**
     * @return The numeric {@code from} date value, e.g. {@code 12970301} (== {@code cei:dateRange/@from}).
     */
    @NotNull
    public NumericDate getNumericFromDate() {
        return numericFromDate;
    }

    /**
     * @return The numeric {@code to} date value, e.g. {@code 12970331} (== {@code cei:dateRange/@from}).
     */
    @NotNull
    public NumericDate getNumericToDate() {
        return numericToDate;
    }

    @Override
    public boolean isValid() {
        return numericFromDate.isValid() && numericToDate.isValid();
    }

    @Override
    public String toString() {

        return "CeiDateRange{" +
                "numericFromDate=" + numericFromDate +
                ", numericToDate=" + numericToDate +
                "} " + super.toString();

    }

}
