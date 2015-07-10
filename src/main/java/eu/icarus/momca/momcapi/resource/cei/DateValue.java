package eu.icarus.momca.momcapi.resource.cei;

import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 10.07.2015.
 */
public class DateValue {

    private final String value;

    public DateValue(@NotNull String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DateValue dateValue = (DateValue) o;

        return value.equals(dateValue.value);

    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean isValid() {
        return value.matches("-?[129]?[0-9][0-9][0-9][019][0-9][01239][0-9]");
    }

    @Override
    public String toString() {
        return "DateValue{" +
                "value='" + value + '\'' +
                '}';
    }

}
