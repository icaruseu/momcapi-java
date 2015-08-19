package eu.icarus.momca.momcapi.xml.cei;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a numeric date value as specified by the
 * <a href="https://github.com/icaruseu/mom-ca/blob/master/my/XRX/src/mom/app/cei/xsd/cei.xsd#L5207">CEI-Schema</a>.<br/>
 * <br/>
 * Example: {@code 12970918}
 *
 * @author Daniel Jeller
 *         Created on 10.07.2015.
 */
public class DateValue {

    private final boolean isValid;
    @NotNull
    private final String value;


    /**
     * Instantiates a new Numeric date.
     *
     * @param dateValue The date value, e.g. {@code 12970918}.
     */
    public DateValue(@NotNull String dateValue) {
        this.value = dateValue;
        this.isValid = validateNumericDate(dateValue);
    }

    /**
     * @param numericDate The numeric date to validate.
     * @return {@code True}, if the numeric date is a valid date as specified by the
     * <a href="https://github.com/icaruseu/mom-ca/blob/master/my/XRX/src/mom/app/cei/xsd/cei.xsd#L5207">CEI-Schema</a>.
     */
    public static boolean validateNumericDate(@NotNull String numericDate) {
        return numericDate.matches("-?[129]?[0-9][0-9][0-9][019][0-9][01239][0-9]");
    }

    /**
     * @return The numeric date value, e.g. {@code 12970918}.
     */
    @NotNull
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DateValue dateValue = (DateValue) o;

        return value.equals(dateValue.value);

    }

    @Override
    @NotNull
    public String toString() {
        return "DateValue{" +
                "value='" + value + '\'' +
                '}';
    }

    /**
     * @return {@code True}, if the numeric date is a valid date as specified by the <a href="https://github.com/icaruseu/mom-ca/blob/master/my/XRX/src/mom/app/cei/xsd/cei.xsd#L5207">CEI-Schema</a>.
     */
    public boolean isValid() {
        return isValid;
    }

}
