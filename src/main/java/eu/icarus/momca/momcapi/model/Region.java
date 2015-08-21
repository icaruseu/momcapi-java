package eu.icarus.momca.momcapi.model;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 11/08/2015.
 */
public class Region {

    @NotNull
    private final Optional<String> code;
    @NotNull
    private final String nativeName;

    public Region(@NotNull String code, @NotNull String nativeName) {
        this.code = code.isEmpty() ? Optional.empty() : Optional.of(code);
        this.nativeName = nativeName;
    }

    @NotNull
    public Optional<String> getCode() {
        return code;
    }

    @NotNull
    public String getNativeName() {
        return nativeName;
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
