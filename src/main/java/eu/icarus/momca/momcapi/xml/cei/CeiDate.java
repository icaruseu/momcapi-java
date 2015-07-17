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
public class CeiDate extends AbstractCeiDate {

    @NotNull
    private final NumericDate numericDate;

    /**
     * Instantiates a new date.
     *
     * @param numericDate The numeric date value, e.g. {@code 12970311}.
     * @param literalDate The literal date value, e.g. {@code 11th March 1297}.
     */
    public CeiDate(@NotNull String numericDate, @NotNull String literalDate) {
        super(new Element("cei:date", Namespace.CEI.getUri()), literalDate);
        addAttribute(new Attribute("value", numericDate));
        this.numericDate = new NumericDate(numericDate);
    }

    @Override
    public boolean couldBeOtherDateType() {

        String dayPart = numericDate.getValue()
                .substring(
                        numericDate.getValue().length() - 2,
                        numericDate.getValue().length());
        String monthPart = numericDate.getValue()
                .substring(
                        numericDate.getValue().length() - 4,
                        numericDate.getValue().length() - 2);

        return monthPart.equals("99") || dayPart.equals("99");

    }

    /**
     * @return The numeric date value, e.g. {@code 12970311} (== {@code cei:date/@value}).
     */
    @NotNull
    public NumericDate getNumericDate() {
        return numericDate;
    }

    @Override
    public boolean isValid() {
        return numericDate.isValid();
    }

    @Override
    public String toString() {
        return "CeiDate{" +
                "numericDate=" + numericDate +
                "} " + super.toString();
    }

}
