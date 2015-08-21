package eu.icarus.momca.momcapi.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 11/08/2015.
 */
public class Country {

    @NotNull
    private final CountryCode countryCode;
    @NotNull
    private final String nativeName;


    public Country(@NotNull CountryCode countryCode, @NotNull String nativeName) {

        if (nativeName.isEmpty()) {
            throw new IllegalArgumentException("The native name is not allowed to be an empty string.");
        }

        this.countryCode = countryCode;
        this.nativeName = nativeName;

    }

    @NotNull
    public CountryCode getCountryCode() {
        return countryCode;
    }

    @NotNull
    public String getNativeName() {
        return nativeName;
    }

    @Override
    public int hashCode() {
        int result = countryCode.hashCode();
        result = 31 * result + nativeName.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (!countryCode.equals(country.countryCode)) return false;
        return nativeName.equals(country.nativeName);

    }

}
