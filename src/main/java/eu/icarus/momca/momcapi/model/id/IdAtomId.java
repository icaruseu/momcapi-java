package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 21/08/2015.
 */
public class IdAtomId extends IdAbstract {

    public IdAtomId(@NotNull Element contentXml, @NotNull String identifier) {
        super(contentXml, identifier);
    }

    @NotNull
    @Override
    public final AtomId getContentXml() {
        AtomId id = ((AtomId) contentXml);
        id.detach();
        return id;
    }

}