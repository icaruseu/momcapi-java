package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the {@code atom:id} of a charter in MOM-CA, e.g.
 * {@code tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232}.
 *
 * @author Daniel Jeller
 *         Created on 25.06.2015.
 */
public class IdCharter extends IdAtomId {

    @NotNull
    private List<String> hierarchicalUriParts = new ArrayList<>();

    public IdCharter(@NotNull AtomId atomId) {

        super(atomId);

        if (getContentXml().getType() != ResourceType.CHARTER) {
            throw new IllegalArgumentException(getContentXml().getText() + " is not a charter atom:id.");
        }
        hierarchicalUriParts = initHierarchicalUriParts(atomId.getText());
    }

    public IdCharter(@NotNull String archiveIdentifier, @NotNull String fondIdentifier, @NotNull String charterIdentifier) {
        super(initAtomIdForFond(archiveIdentifier, fondIdentifier, charterIdentifier));
        hierarchicalUriParts = initHierarchicalUriParts(getContentXml().getText());
    }

    public IdCharter(@NotNull String collectionIdentifier, @NotNull String charterIdentifier) {
        super(initAtomIdForCollection(collectionIdentifier, charterIdentifier));
        hierarchicalUriParts = initHierarchicalUriParts(getContentXml().getText());
    }

    @NotNull
    public List<String> getHierarchicalUriParts() {
        return hierarchicalUriParts;
    }

    @NotNull
    public String getHierarchicalUriPartsAsString() {
        return String.join("/", hierarchicalUriParts.toArray(new String[hierarchicalUriParts.size()]));
    }


    private static AtomId initAtomIdForCollection(@NotNull String collectionIdentifier, @NotNull String charterIdentifier) {

        if (collectionIdentifier.isEmpty() || charterIdentifier.isEmpty()) {
            throw new IllegalArgumentException("The identifiers are not allowed to be empty strings.");
        }

        if (collectionIdentifier.contains("/") || charterIdentifier.contains("/")) {
            throw new IllegalArgumentException("One of the identifiers contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/",
                AtomId.DEFAULT_PREFIX,
                ResourceType.CHARTER.getNameInId(),
                collectionIdentifier,
                charterIdentifier));

    }

    private static AtomId initAtomIdForFond(@NotNull String archiveIdentifier, @NotNull String fondIdentifier, @NotNull String charterIdentifier) {

        if (archiveIdentifier.isEmpty() || fondIdentifier.isEmpty() || charterIdentifier.isEmpty()) {
            throw new IllegalArgumentException("The identifiers are not allowed to be empty strings.");
        }

        if (archiveIdentifier.contains("/") || fondIdentifier.contains("/")) {
            throw new IllegalArgumentException("One of the identifiers contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/",
                AtomId.DEFAULT_PREFIX,
                ResourceType.CHARTER.getNameInId(),
                archiveIdentifier,
                fondIdentifier,
                charterIdentifier));

    }

    private List<String> initHierarchicalUriParts(String text) {

        text = text.replace("tag:www.monasterium.net,2011:/", "");
        String[] parts = Util.decode(text).split("/");

        List<String> allParts = new ArrayList<>(0);
        allParts.addAll(Arrays.asList(parts));
        allParts.remove(0);
        allParts.remove(allParts.size() - 1);

        return allParts;

    }

    public boolean isInFond() {
        return hierarchicalUriParts.size() == 2;
    }



}
