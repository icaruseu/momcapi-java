package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.resource.Fond;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 09/08/2015.
 */
public class FondManager extends AbstractManager {

    FondManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    public void addFond(@NotNull Fond fond) {

        if (momcaConnection.isResourceExisting(fond.getUri())) {
            throw new IllegalArgumentException(String.format("The fond '%s' is already existing in the database.", fond.getId()));
        }

        String identifier = fond.getIdentifier();
        String archiveIdentifier = fond.getArchiveId().getIdentifier();
        String parentUri = ResourceRoot.ARCHIVAL_FONDS.getUri() + "/" + archiveIdentifier;

        momcaConnection.createCollection(archiveIdentifier, ResourceRoot.ARCHIVAL_FONDS.getUri());
        momcaConnection.createCollection(identifier, parentUri);

        String time = momcaConnection.queryRemoteDateTime();

        momcaConnection.writeAtomResource(fond, time, time);
        fond.getFondPreferences().ifPresent(momcaConnection::writeExistResource);

    }

    public void deleteFond(@NotNull IdFond idFond) {

        if (!momcaConnection.getCharterManager().listChartersPublic(idFond).isEmpty()
                || !momcaConnection.getCharterManager().listChartersImport(idFond).isEmpty()) {
            throw new IllegalArgumentException("There are still existing charters for fond '" + idFond.getIdentifier() + "'");
        }

        momcaConnection.deleteCollection(
                String.format("%s/%s/%s",
                        ResourceRoot.PUBLIC_CHARTERS.getUri(),
                        idFond.getIdArchive().getIdentifier(),
                        idFond.getIdentifier()));

        momcaConnection.deleteCollection(
                String.format("%s/%s/%s",
                        ResourceRoot.ARCHIVAL_FONDS.getUri(),
                        idFond.getIdArchive().getIdentifier(),
                        idFond.getIdentifier()));

    }

    @NotNull
    public Optional<Fond> getFond(@NotNull IdFond idFond) {

        Optional<Fond> fond = Optional.empty();

        Optional<ExistResource> fondResource = getFirstMatchingExistResource(idFond.getContentXml());

        if (fondResource.isPresent()) {

            String prefsUrl = fondResource.get().getUri().replace("ead", "preferences");
            Optional<ExistResource> fondPrefs = getExistResource(prefsUrl);

            fond = Optional.of(new Fond(fondResource.get(), fondPrefs));

        }

        return fond;

    }

    public boolean isFondExisting(@NotNull IdFond idFond) {

        String identifier = idFond.getIdentifier();
        String resourceName = idFond.getIdentifier() + ResourceType.FOND.getNameSuffix();
        String archiveIdentifier = idFond.getIdArchive().getIdentifier();
        String rootUri = ResourceRoot.ARCHIVAL_FONDS.getUri();
        String uri = String.join("/", rootUri, archiveIdentifier, identifier, resourceName);

        return momcaConnection.isResourceExisting(uri);

    }

    @NotNull
    public List<IdFond> listFonds(@NotNull IdArchive idArchive) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listFonds(idArchive));
        return queryResults.stream().map(AtomId::new).map(IdFond::new).collect(Collectors.toList());
    }

}
