package eu.icarus.momca.momcapi.xml.cei;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a numeric date value as specified by the <a href="https://github.com/icaruseu/mom-ca/blob/master/my/XRX/src/mom/app/cei/xsd/cei.xsd#L5207">CEI-Schema</a>.<br/><br/>
 * Example: {@code 12970918}
 *
 * @author Daniel Jeller
 *         Created on 10.07.2015.
 */
public class NumericDate {

    private final boolean isValid;
    @NotNull
    private final String value;


    /**
     * Instantiates a new Numeric date.
     *
     * @param value The date value, e.g. {@code 12970918}.
     */
    public NumericDate(@NotNull String value) {
        this.value = value;
        this.isValid = validateNumericDate(value);
    }

    /**
     * @param numericDate The numeric date to validate.
     * @return {@code True}, if the numeric date is a valid date as specified by the <a href="https://github.com/icaruseu/mom-ca/blob/master/my/XRX/src/mom/app/cei/xsd/cei.xsd#L5207">CEI-Schema</a>.
     */
    public static boolean validateNumericDate(@NotNull String numericDate) {
        return numericDate.matches("-?[129]?[0-9][0-9][0-9][019][0-9][01239][0-9]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumericDate numericDate = (NumericDate) o;

        return value.equals(numericDate.value);

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

    /**
     * @return {@code True}, if the numeric date is a valid date as specified by the <a href="https://github.com/icaruseu/mom-ca/blob/master/my/XRX/src/mom/app/cei/xsd/cei.xsd#L5207">CEI-Schema</a>.
     */
    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return "NumericDate{" +
                "value='" + value + '\'' +
                '}';
    }

}
