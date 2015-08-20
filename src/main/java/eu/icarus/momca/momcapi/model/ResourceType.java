package eu.icarus.momca.momcapi.model;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The type of a resource used in MOM-CA.
 *
 * @author Daniel Jeller
 *         Createdon 25.06.2015.
 */
public enum ResourceType {

    ANNOTATION_IMAGE("annotation-image", ".xml", 5, 5),
    ARCHIVE("archive", ".eag.xml", 3, 3),
    CHARTER("charter", ".charter.xml", 4, 5),
    COLLECTION("collection", ".cei.xml", 3, 3),
    FOND("fond", ".ead.xml", 4, 4),
    MY_COLLECTION("mycollection", ".mycollection.xml", 3, 3),
    SVG("svg", ".xml", 5, 5);

    private final int maxIdParts;
    private final int minIdParts;
    private final String nameInId;
    private final String nameSuffix;

    ResourceType(String nameInId, String resourceNameSuffix, int minIdParts, int maxIdParts) {
        this.nameInId = nameInId;
        this.nameSuffix = resourceNameSuffix;
        this.minIdParts = minIdParts;
        this.maxIdParts = maxIdParts;
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

    public int getMaxIdParts() {
        return maxIdParts;
    }

    public int getMinIdParts() {
        return minIdParts;
    }

    /**
     * @return The type as used in an {@code atom:id} in MOM-CA, e.g. {@code charter} as in
     * {@code tag:www.monasterium.net,2011:/}<b>{@code charter}</b>{@code /CH-KAE/Urkunden/KAE_Urkunde_Nr_1}
     */
    public String getNameInId() {
        return nameInId;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }
}
