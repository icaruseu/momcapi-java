package eu.icarus.momca.momcapi.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by daniel on 22.07.2015.
 */
public class Address {

    @NotNull
    private final String municipality;
    @NotNull
    private final String postalcode;
    @NotNull
    private final String street;

    public Address(@NotNull String municipality, @NotNull String postalcode, @NotNull String street) {
        this.street = street;
        this.postalcode = postalcode;
        this.municipality = municipality;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (!municipality.equals(address.municipality)) return false;
        if (!postalcode.equals(address.postalcode)) return false;
        return street.equals(address.street);

    }

    @NotNull
    public String getMunicipality() {
        return municipality;
    }

    @NotNull
    public String getPostalcode() {
        return postalcode;
    }

    @NotNull
    public String getStreet() {
        return street;
    }

    @Override
    public int hashCode() {
        int result = municipality.hashCode();
        result = 31 * result + postalcode.hashCode();
        result = 31 * result + street.hashCode();
        return result;
    }

    public boolean isEmpty() {
        return getStreet().isEmpty() && getMunicipality().isEmpty() && getMunicipality().isEmpty();
    }

    @Override
    @NotNull
    public String toString() {
        return "Address{" +
                "municipality='" + municipality + '\'' +
                ", postalcode='" + postalcode + '\'' +
                ", street='" + street + '\'' +
                '}';
    }

}
