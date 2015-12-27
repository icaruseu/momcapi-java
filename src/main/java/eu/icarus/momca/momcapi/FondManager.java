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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 09/08/2015.
 */
public class FondManager extends AbstractManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FondManager.class);

    FondManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    public boolean addFond(@NotNull Fond fond) {

        IdFond id = fond.getId();

        LOGGER.info("Trying to add fond '{}' to the database.", id);

        boolean proceed = true;

        if (momcaConnection.isResourceExisting(fond.getUri())) {
            proceed = false;
            LOGGER.info("The fond '{}' is already existing in the database.", id);
        }

        boolean success = false;

        if (proceed) {

            String uri = createUriFromId(id);
            success = momcaConnection.makeSureCollectionPathExists(uri);

            if (success) {

                String time = momcaConnection.queryRemoteDateTime();
                success = momcaConnection.writeAtomResource(fond, time, time);

                if (success && fond.getFondPreferences().isPresent()) {
                    momcaConnection.writeExistResource(fond.getFondPreferences().get());
                }

                if (success) {
                    LOGGER.info("Fond '{}' added to the database.", id);
                } else {
                    LOGGER.info("Failed to add fond '{}' to the database.", id);
                }

            } else {

                LOGGER.info("Failed to assure the parent path, '{}', for fond '{}' exists. Aborting addition.", uri, id);

            }

        }

        return success;

    }

    private String createUriFromId(IdFond id) {
        return String.format("%s/%s/%s", ResourceRoot.ARCHIVAL_FONDS.getUri(), id.getIdArchive().getIdentifier(), id.getIdentifier());
    }

    public void deleteFond(@NotNull IdFond idFond) {

        if (!momcaConnection.getCharterManager().listPublicCharters(idFond).isEmpty()
                || !momcaConnection.getCharterManager().listImportedCharters(idFond).isEmpty()) {
            throw new IllegalArgumentException("There are still existing charters for fond '" + idFond.getIdentifier() + "'");
        }

        momcaConnection.deleteCollection(
                String.format("%s/%s/%s",
                        ResourceRoot.PUBLIC_CHARTERS.getUri(),
                        idFond.getIdArchive().getIdentifier(),
                        idFond.getIdentifier()));

        String uri = createUriFromId(idFond);
        momcaConnection.deleteCollection(uri);

    }

    @NotNull
    public Optional<Fond> getFond(@NotNull IdFond idFond) {

        Optional<Fond> fond = Optional.empty();

        Optional<ExistResource> fondResource = getFirstMatchingExistResource(idFond.getContentAsElement());

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
