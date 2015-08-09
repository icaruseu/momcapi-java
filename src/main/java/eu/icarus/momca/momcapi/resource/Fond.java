package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.atom.IdFond;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 17.07.2015.
 */
public class Fond extends MomcaResource {

    @NotNull
    private final FondPreferences fondPreferences;
    @NotNull
    private final IdFond id;

    public Fond(@NotNull MomcaResource fondResource, @NotNull FondPreferences fondPreferences) {
        super(fondResource);
        id = initId();
        this.fondPreferences = fondPreferences;
    }

    @NotNull
    public IdFond getId() {
        return id;
    }

    private IdFond initId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", getXmlAsDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdFond(idString);
        }

    }

}
