package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.exception.MomcaException;
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

    private static final String LOCAL_NAME = "dateRange";
    @NotNull
    private DateValue fromDateValue;
    @NotNull
    private DateValue toDateValue;


    public DateRange(@NotNull String fromDateValue, @NotNull String toDateValue, @NotNull String literalDate,
                     @NotNull String certainty, @NotNull String lang, @NotNull String facs, @NotNull String id,
                     @NotNull String n) {

        super(LOCAL_NAME, literalDate, certainty, facs, id, lang, n);

        this.fromDateValue = new DateValue(fromDateValue);
        this.toDateValue = new DateValue(toDateValue);

        initAttributes(fromDateValue, toDateValue);

    }

    public DateRange(@NotNull String fromDateValue, @NotNull String toDateValue, @NotNull String literalDate) {
        this(fromDateValue, toDateValue, literalDate, "", "", "", "", "");
    }

    public DateRange(@NotNull Element dateElement) {

        super(LOCAL_NAME, dateElement);

        String from = dateElement.getAttributeValue("from");
        String to = dateElement.getAttributeValue("to");

        if ((from == null || from.isEmpty()) || (to == null || to.isEmpty())) {
            String message = String.format(
                    "At least either 'to' or 'from' element must be present in the 'dateRange' Element `%s`",
                    dateElement.toXML());
            throw new MomcaException(message);
        }

        this.fromDateValue = new DateValue(from);
        this.toDateValue = new DateValue(to);

        initAttributes(from, to);

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

    private void initAttributes(@NotNull String fromDateValue, @NotNull String toDateValue) {

        if (!fromDateValue.isEmpty()) {
            addAttribute(new Attribute("from", fromDateValue));
        }

        if (!toDateValue.isEmpty()) {
            addAttribute(new Attribute("to", toDateValue));
        }

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
