package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The type of a resource used in MOM-CA.
 *
 * @author Daniel Jeller
 *         Createdon 25.06.2015.
 */
public enum ResourceType {

    ANNOTATION_IMAGE("annotation-image"), ARCHIVE("archive"), CHARTER("charter"), COLLECTION("collection"), FOND("fond"), SVG("svg");

    private final String atomIdName;

    ResourceType(String atomIdName) {
        this.atomIdName = atomIdName;
    }

    @NotNull
    public static ResourceType createFromValue(@NotNull String value) {

        Optional<ResourceType> type = Optional.empty();

        for (ResourceType t : ResourceType.values()) {
            if (value.equals(t.getAtomIdName())) {
                type = Optional.of(t);
            }
        }

        return type.orElseThrow(IllegalArgumentException::new);

    }

    /**
     * @return The name of the type as used in an {@code atom:id} used in MOM-CA, e.g. {@code charter} as in  {@code tag:www.monasterium.net,2011:/}<b>{@code charter}</b>{@code /CH-KAE/Urkunden/KAE_Urkunde_Nr_1}
     */
    public String getAtomIdName() {
        return atomIdName;
    }

}
