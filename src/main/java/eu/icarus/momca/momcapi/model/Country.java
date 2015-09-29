package eu.icarus.momca.momcapi.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 11/08/2015.
 */
public class Country {

    @NotNull
    private CountryCode countryCode;
    @NotNull
    private String nativeName;

    public Country(@NotNull CountryCode countryCode, @NotNull String nativeName) {

        this.countryCode = countryCode;

        if (nativeName.isEmpty()) {
            throw new IllegalArgumentException("The native name is not allowed to be an empty string.");
        }
        this.nativeName = nativeName;

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        return countryCode.equals(country.countryCode) && nativeName.equals(country.nativeName);

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

    public void setCountryCode(@NotNull CountryCode countryCode) {
        this.countryCode = countryCode;
    }

    public void setNativeName(@NotNull String nativeName) {

        if (nativeName.isEmpty()) {
            throw new IllegalArgumentException("The native name is not allowed to be an empty string.");
        }

        this.nativeName = nativeName;

    }

}
