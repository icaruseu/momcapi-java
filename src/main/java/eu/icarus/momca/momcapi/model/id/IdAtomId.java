package eu.icarus.momca.momcapi.model.id;

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
        super(atomIdXml, atomIdXml.getText().split("/")[atomIdXml.getText().split("/").length - 1]);
    }

    @NotNull
    @Override
    public final AtomId getContentXml() {
        AtomId id = ((AtomId) contentXml);
        id.detach();
        return id;
    }

    public final ResourceType getType() {
        return getContentXml().getType();
    }

}