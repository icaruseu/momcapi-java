package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 09/08/2015.
 */
public enum ImageAccess {

    FREE("free"), RESTRICTED("restricted");

    @NotNull
    private final String text;

    ImageAccess(@NotNull String text) {
        this.text = text;
    }

    @NotNull
    public static ImageAccess fromText(String text) {

        ImageAccess result = null;

        for (ImageAccess ia : ImageAccess.values()) {
            if (ia.getText().equals(text)) {
                result = ia;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("No matching imageAccess found.");
        }

        return result;
    }

    @NotNull
    public String getText() {
        return text;
    }

}
