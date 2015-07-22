package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import org.jetbrains.annotations.NotNull;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Archive extends MomcaResource {

    @NotNull
    private final IdArchive atomId;

    public Archive(@NotNull MomcaResource momcaResource) {
        super(momcaResource);
        atomId = initAtomId();
    }

    @NotNull
    public IdArchive getAtomId() {
        return atomId;
    }

    private IdArchive initAtomId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", getXmlAsDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdArchive(idString);
        }

    }

}
