package eu.icarus.momca.momcapi.xml.cei;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 10.07.2015.
 */
public class CeiDateRange extends AbstractCeiDate {

    @NotNull
    private final DateValue fromValue;
    @NotNull
    private final DateValue toValue;

    public CeiDateRange(@NotNull String fromValue, @NotNull String toValue, @NotNull String literalDate) {

        super(new Element("cei:dateRange", Namespace.CEI.getUri()), literalDate);
        addAttribute(new Attribute("from", fromValue));
        addAttribute(new Attribute("to", toValue));

        this.fromValue = new DateValue(fromValue);
        this.toValue = new DateValue(toValue);

    }

    @NotNull
    public DateValue getFromValue() {
        return fromValue;
    }

    @NotNull
    public String getFromValueAsString() {
        return fromValue.getValue();
    }

    @NotNull
    public DateValue getToValue() {
        return toValue;
    }

    @NotNull
    public String getToValueAsString() {
        return toValue.getValue();
    }

    @Override
    public boolean isValid() {
        return fromValue.isValid() && toValue.isValid();
    }

    @Override
    public boolean isWrongDateType() {
        return fromValue.equals(toValue);
    }

    @Override
    public String toString() {

        return "CeiDateRange{" +
                "fromValue=" + fromValue +
                ", toValue=" + toValue +
                "} " + super.toString();

    }

}
