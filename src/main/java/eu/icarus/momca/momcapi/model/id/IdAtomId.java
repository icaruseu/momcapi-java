package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 21/08/2015.
 */
public class IdAtomId extends IdAbstract {

    @Deprecated
    public IdAtomId(@NotNull AtomId atomIdXml, @NotNull String identifier) {
        super(atomIdXml, identifier);
    }

    public IdAtomId(@NotNull AtomId atomIdXml) {
        super(atomIdXml, initIdentifier(atomIdXml));
    }

    @NotNull
    private static String initIdentifier(@NotNull AtomId atomId) {
        String[] idParts = atomId.getText().split("/");
        return Util.decode(idParts[idParts.length - 1]);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdAtomId otherId = (IdAtomId) o;

        return getContentXml().getText().equals(otherId.getContentXml().getText());

    }

    @NotNull
    @Override
    public final AtomId getContentXml() {
        return ((AtomId) contentXml);
    }

    public final ResourceType getType() {
        return getContentXml().getType();
    }

    @Override
    public int hashCode() {
        return getContentXml().getText().hashCode();
    }

    @Override
    public String toString() {
        return "IdAtomId{" + getContentXml().getText() + "}";
    }

}