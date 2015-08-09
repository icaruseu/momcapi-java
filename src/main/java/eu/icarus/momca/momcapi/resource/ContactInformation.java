package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by daniel on 22.07.2015.
 */
public class ContactInformation {

    @NotNull
    private final String email;
    @NotNull
    private final String fax;
    @NotNull
    private final String telephone;
    @NotNull
    private final String webpage;

    public ContactInformation(@NotNull String webpage, @NotNull String fax, @NotNull String telephone, @NotNull String email) {
        this.telephone = telephone;
        this.fax = fax;
        this.email = email;
        this.webpage = webpage;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInformation that = (ContactInformation) o;

        if (!email.equals(that.email)) return false;
        if (!fax.equals(that.fax)) return false;
        if (!telephone.equals(that.telephone)) return false;
        return webpage.equals(that.webpage);

    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @NotNull
    public String getFax() {
        return fax;
    }

    @NotNull
    public String getTelephone() {
        return telephone;
    }

    @NotNull
    public String getWebpage() {
        return webpage;
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + fax.hashCode();
        result = 31 * result + telephone.hashCode();
        result = 31 * result + webpage.hashCode();
        return result;
    }

    @Override
    @NotNull
    public String toString() {
        return "ContactInformation{" +
                "email='" + email + '\'' +
                ", fax='" + fax + '\'' +
                ", telephone='" + telephone + '\'' +
                ", webpage='" + webpage + '\'' +
                '}';
    }
}
