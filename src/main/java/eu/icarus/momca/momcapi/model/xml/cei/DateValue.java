package eu.icarus.momca.momcapi.model.xml.cei;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DateValue dateValue = (DateValue) o;

        return value.equals(dateValue.value);

    }

    @NotNull
    public Optional<Integer> getDay() {

        Optional<Integer> day = Optional.empty();

        if (isValid()) {

            String dayString = value.substring(value.length() - 2, value.length());

            if (!dayString.equals("99") && !dayString.equals("00")) {
                day = Optional.of(Integer.parseInt(dayString));
            }

        }

        return day;

    }

    @NotNull
    public Optional<Integer> getMonth() {

        Optional<Integer> month = Optional.empty();

        if (isValid()) {

            String monthString = value.substring(value.length() - 4, value.length() - 2);

            if (!monthString.equals("99") && !monthString.equals("00")) {
                month = Optional.of(Integer.parseInt(monthString));
            }

        }

        return month;

    }

    /**
     * @return The numeric date value, e.g. {@code 12970918}.
     */
    @NotNull
    public String getValue() {
        return value;
    }

    @NotNull
    public Optional<Integer> getYear() {

        Optional<Integer> year = Optional.empty();

        if (isValid()) {

            String yearString = value.substring(0, value.length() - 4);

            if (!yearString.equals("9999")) {
                year = Optional.of(Integer.parseInt(yearString));
            }

        }

        return year;

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
    @NotNull
    public String toString() {
        return "DateValue{" +
                "value='" + value + '\'' +
                '}';
    }

}
