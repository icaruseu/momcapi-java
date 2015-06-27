package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by daniel on 25.06.2015.
 */
public enum ResourceType {

    ANNOTATION_IMAGE("annotation-image"), ARCHIVE("archive"), CHARTER("charter"), COLLECTION("collection"), FOND("fond"), SVG("svg");

    private final String value;

    ResourceType(String value) {
        this.value = value;
    }

    @NotNull
    public static ResourceType createFromValue(@NotNull String value) {

        Optional<ResourceType> type = Optional.empty();

        for (ResourceType t : ResourceType.values()) {
            if (value.equals(t.getValue())) {
                type = Optional.of(t);
            }
        }

        return type.orElseThrow(IllegalArgumentException::new);

    }

    public String getValue() {
        return value;
    }

}
