package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
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
public class DateRange extends DateAbstract {

    @NotNull
    private final DateValue fromDateValue;
    @NotNull
    private final DateValue toDateValue;


    /**
     * Instantiates a new ceiDateRange.
     *
     * @param fromDateValue The numeric {@code from} date value, e.q. {@code 12970301}.
     * @param toDateValue   The numeric {@code to} date value, e.q. {@code 12970331}.
     * @param literalDate   The literal date value, e.g. {@code March 1297}.
     */
    public DateRange(@NotNull String fromDateValue, @NotNull String toDateValue, @NotNull String literalDate) {

        super(new Element("cei:dateRange", Namespace.CEI.getUri()), literalDate);

        addAttribute(new Attribute("from", fromDateValue));
        addAttribute(new Attribute("to", toDateValue));

        this.fromDateValue = new DateValue(fromDateValue);
        this.toDateValue = new DateValue(toDateValue);

    }

    public DateRange(@NotNull String fromDateValue, @NotNull String toDateValue, @NotNull String literalDate,
                     @NotNull String certainty, @NotNull String lang, @NotNull String facs, @NotNull String id,
                     @NotNull String n) {

        super(new Element("cei:dateRange", Namespace.CEI.getUri()), literalDate, certainty, lang, facs, id, n);

        addAttribute(new Attribute("from", fromDateValue));
        addAttribute(new Attribute("to", toDateValue));

        this.fromDateValue = new DateValue(fromDateValue);
        this.toDateValue = new DateValue(toDateValue);

    }


    @Override
    public boolean couldBeOtherDateType() {
        return fromDateValue.equals(toDateValue);
    }

    /**
     * @return The numeric {@code from} date value, e.g. {@code 12970301} (== {@code cei:dateRange/@from}).
     */
    @NotNull
    public DateValue getFromDateValue() {
        return fromDateValue;
    }

    /**
     * ^
     *
     * @return The numeric {@code to} date value, e.g. {@code 12970331} (== {@code cei:dateRange/@from}).
     */
    @NotNull
    public DateValue getToDateValue() {
        return toDateValue;
    }

    @Override
    public boolean isUndated() {

        return !getFromDateValue().getYear().isPresent() && !getToDateValue().getYear().isPresent();


    }

    @Override
    public boolean isValid() {
        return fromDateValue.isValid() && toDateValue.isValid();
    }

    @Override
    @NotNull
    public String toString() {

        return "DateRange{" +
                "fromDateValue=" + fromDateValue +
                ", toDateValue=" + toDateValue +
                "} " + super.toString();

    }

}
