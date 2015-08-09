package eu.icarus.momca.momcapi.xml.cei;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single-value {@code cei:date} element as defined in the
 * <a href="http://www.cei.lmu.de/element.php?ID=123">CEI specification</a>. It consists of a literal date as well
 * as a numeric date value.<br/>
 * <br/>
 * Example in XML-form:<br/>
 * {@code <cei:date value="12970311">11th March 1297</cei:date>}
 *
 * @author Daniel Jeller
 *         Created on 10.07.2015.
 */
public class Date extends DateAbstract {

    @NotNull
    private final DateValue dateValue;

    /**
     * Instantiates a new date.
     *
     * @param numericDate The numeric date value, e.g. {@code 12970311}.
     * @param literalDate The literal date value, e.g. {@code 11th March 1297}.
     */
    public Date(@NotNull String numericDate, @NotNull String literalDate) {
        super(new Element("cei:date", Namespace.CEI.getUri()), literalDate);
        addAttribute(new Attribute("value", numericDate));
        this.dateValue = new DateValue(numericDate);
    }

    @Override
    public boolean couldBeOtherDateType() {

        String dayPart = dateValue.getValue()
                .substring(
                        dateValue.getValue().length() - 2,
                        dateValue.getValue().length());
        String monthPart = dateValue.getValue()
                .substring(
                        dateValue.getValue().length() - 4,
                        dateValue.getValue().length() - 2);

        return monthPart.equals("99") || dayPart.equals("99");

    }

    /**
     * @return The numeric date value, e.g. {@code 12970311} (== {@code cei:date/@value}).
     */
    @NotNull
    public DateValue getDateValue() {
        return dateValue;
    }

    @Override
    public boolean isValid() {
        return dateValue.isValid();
    }

    @Override
    @NotNull
    public String toString() {
        return "Date{" +
                "dateValue=" + dateValue +
                "} " + super.toString();
    }

}
