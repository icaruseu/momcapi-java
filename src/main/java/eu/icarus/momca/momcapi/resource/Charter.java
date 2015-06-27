package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 25.06.2015.
 */
public class Charter extends ExistResource {

    @NotNull
    private final CharterAtomId charterAtomId;

    public Charter(@NotNull ExistResource existResource, @NotNull CharterAtomId charterAtomId) {
        super(existResource);
        this.charterAtomId = charterAtomId;
    }

    @NotNull
    public CharterAtomId getCharterAtomId() {
        return charterAtomId;
    }

    @Override
    public String toString() {
        return "Charter{" +
                "charterAtomId=" + charterAtomId +
                "} " + super.toString();
    }

}
