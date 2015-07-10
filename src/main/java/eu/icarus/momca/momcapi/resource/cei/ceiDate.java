package eu.icarus.momca.momcapi.resource.cei;

import eu.icarus.momca.momcapi.resource.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 10.07.2015.
 */
public class CeiDate extends AbstractCeiDate {

    @NotNull
    private final DateValue dateValue;

    public CeiDate(@NotNull String dateValue, @NotNull String literalDate) {
        super(new Element("cei:date", Namespace.CEI.getUri()), literalDate);
        addAttribute(new Attribute("value", dateValue));
        this.dateValue = new DateValue(dateValue);
    }

    @NotNull
    public DateValue getDateValue() {
        return dateValue;
    }

    @NotNull
    public String getDateValueAsString() {
        return dateValue.getValue();
    }

    @Override
    public boolean isValid() {
        return dateValue.isValid();
    }

    @Override
    public String toString() {
        return "CeiDate{" +
                "dateValue=" + dateValue +
                "} " + super.toString();
    }

}
