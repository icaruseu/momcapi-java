package eu.icarus.momca.momcapi.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 11/08/2015.
 */
public class Region {

    @NotNull
    private Optional<String> code = Optional.empty();
    @NotNull
    private String nativeName;

    public Region(@Nullable String code, @NotNull String nativeName) {
        setNativeName(nativeName);
        setCode(code);
    }

    public Region(@NotNull String nativeName) {
        this(null, nativeName);
    }

    @NotNull
    public Optional<String> getCode() {
        return code;
    }

    public void setCode(@Nullable String code) {

        if (code == null || code.isEmpty()) {
            this.code = Optional.empty();
        } else {
            this.code = Optional.of(code);
        }

    }

    @NotNull
    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(@NotNull String nativeName) {

        if (nativeName.isEmpty()) {
            throw new IllegalArgumentException("The native name is not allowed to be an empty string.");
        }
        this.nativeName = nativeName;

    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + nativeName.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        if (!code.equals(region.code)) return false;
        return nativeName.equals(region.nativeName);

    }
}
