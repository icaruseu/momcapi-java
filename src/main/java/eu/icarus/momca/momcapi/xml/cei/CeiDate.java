package eu.icarus.momca.momcapi.xml.cei;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single-value date as specified in the <a href="http://www.cei.lmu.de/element.php?ID=123">CEI specification</a>. It consists of a literal date as well as a numeric date value.<br/><br/>
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
     * @param literalDate the literal date value, e.g. {@code 11th March 1297}.
     */
    public CeiDate(@NotNull String numericDate, @NotNull String literalDate) {
        super(new Element("cei:date", Namespace.CEI.getUri()), literalDate);
        addAttribute(new Attribute("value", numericDate));
        this.numericDate = new NumericDate(numericDate);
    }

    /**
     * @return The numeric date value (== {@code cei:date/@value}).
     */
    @NotNull
    public NumericDate getNumericDate() {
        return numericDate;
    }

    /**
     * @return The numeric date value as a {@code String}.
     */
    @NotNull
    public String getNumericDateAsString() {
        return numericDate.getValue();
    }

    @Override
    public boolean isValid() {
        return numericDate.isValid();
    }

    @Override
    public boolean isWrongDateType() {
        String dayPart = getNumericDateAsString().substring(getNumericDateAsString().length() - 2, getNumericDateAsString().length());
        String monthPart = getNumericDateAsString().substring(getNumericDateAsString().length() - 4, getNumericDateAsString().length() - 2);
        return monthPart.equals("99") || dayPart.equals("99");
    }

    @Override
    public String toString() {
        return "CeiDate{" +
                "numericDate=" + numericDate +
                "} " + super.toString();
    }

}
