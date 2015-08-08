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

    ANNOTATION_IMAGE("annotation-image"),
    ARCHIVE("archive"),
    CHARTER("charter"),
    COLLECTION("collection"),
    FOND("fond"),
    MY_COLLECTION("mycollection"),
    SVG("svg");

    private final String nameInId;

    ResourceType(String nameInId) {
        this.nameInId = nameInId;
    }

    /**
     * @param value A value to find the corresponding ResourceType to.
     * @return The ResourceType corresponding to {@code value}, e.g. {@code archive} returns {@code ARCHIVE}.
     */
    @NotNull
    public static ResourceType createFromValue(@NotNull String value) {

        Optional<ResourceType> type = Optional.empty();

        for (ResourceType t : ResourceType.values()) {
            if (value.equals(t.getNameInId())) {
                type = Optional.of(t);
            }
        }

        return type.orElseThrow(IllegalArgumentException::new);

    }

    /**
     * @return The type as used in an {@code atom:id} in MOM-CA, e.g. {@code charter} as in
     * {@code tag:www.monasterium.net,2011:/}<b>{@code charter}</b>{@code /CH-KAE/Urkunden/KAE_Urkunde_Nr_1}
     */
    public String getNameInId() {
        return nameInId;
    }

}
