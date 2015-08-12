package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 11/08/2015.
 */
public class CountryCode {

    @NotNull
    private final String code;

    public CountryCode(@NotNull String code) {
        if (code.length() != 2 || !code.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("'" + code + "' is not a valid 'ISO 3166-1 alpha-2' country code.");
        }
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryCode that = (CountryCode) o;

        return code.equals(that.code);

    }

    @NotNull
    public String getCode() {
        return code;
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
