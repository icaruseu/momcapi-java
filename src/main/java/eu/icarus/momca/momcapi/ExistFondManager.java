package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.CharterStatus;
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
public class ExistFondManager extends AbstractExistManager implements FondManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistFondManager.class);

    ExistFondManager(@NotNull ExistMomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @Override
    public boolean add(@NotNull Fond fond) {

        IdFond id = fond.getId();

        LOGGER.info("Trying to add fond '{}' to the database.", id);

        boolean proceed = true;

        if (isFondExisting(id)) {
            proceed = false;
            LOGGER.info("The fond '{}' is already existing in the database. Aborting addition.", id);
        }

        if (proceed && !momcaConnection.getArchiveManager().isExisting(fond.getArchiveId())) {
            proceed = false;
            LOGGER.info("The archive, '{}', the fond '{}' is added to doesn't exist. Aborting addition.", fond.getArchiveId(), id);
        }

        boolean success = false;

        if (proceed) {

            String uri = createCollectionUri(ResourceRoot.ARCHIVAL_FONDS, id);
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

    @NotNull
    private String createCollectionUri(@NotNull ResourceRoot resourceRoot, @NotNull IdFond id) {

        return String.format("%s/%s/%s", resourceRoot.getUri(), id.getIdArchive().getIdentifier(), id.getIdentifier());

    }

    @NotNull
    private String createEadResourceUri(@NotNull IdFond id) {

        return String.format("%s/%s/%s/%s%s",
                ResourceRoot.ARCHIVAL_FONDS.getUri(),
                id.getIdArchive().getIdentifier(),
                id.getIdentifier(),
                id.getIdentifier(),
                ResourceType.FOND.getNameSuffix());

    }

    @NotNull
    private String createPrefsUri(@NotNull Optional<ExistResource> fondResource) {

        return fondResource.get().getUri().replace("ead", "preferences");

    }

    @Override
    public boolean delete(@NotNull IdFond idFond) {

        LOGGER.info("Trying to delete the fond '{}'.", idFond);

        boolean proceed = true;

        if (!isFondExisting(idFond)) {
            proceed = false;
            LOGGER.info("The fond '{}' is not existing. Aborting deletion.", idFond);
        }

        if (proceed && !momcaConnection.getCharterManager().list(idFond).isEmpty()
                || !momcaConnection.getCharterManager().list(idFond, CharterStatus.IMPORTED).isEmpty()) {
            proceed = false;
            LOGGER.info("There are still existing charters for fond '{}'. Aborting deletion.", idFond);
        }

        boolean success = false;

        if (proceed) {

            String fondCollectionUri = createCollectionUri(ResourceRoot.ARCHIVAL_FONDS, idFond);
            success = momcaConnection.deleteCollection(fondCollectionUri);

            if (success) {

                String charterCollectionUri = createCollectionUri(ResourceRoot.PUBLIC_CHARTERS, idFond);
                success = momcaConnection.deleteCollection(charterCollectionUri);

                if (success) {
                    LOGGER.info("Fond '{}' deleted.", idFond);
                } else {
                    success = true;
                    LOGGER.info("Deleted fond '{}' but failed to delete empty charters' collection at '{}'.", idFond, charterCollectionUri);
                }

            } else {
                LOGGER.info("Failed to delete fond '{}'.", idFond);
            }

        }

        return success;

    }

    @Override
    @NotNull
    public Optional<Fond> get(@NotNull IdFond idFond) {

        LOGGER.info("Trying to get fond '{}' from the database.", idFond);

        Optional<Fond> fond = Optional.empty();

        String eadUri = createEadResourceUri(idFond);
        Optional<ExistResource> fondResource = momcaConnection.readExistResource(eadUri);

        if (fondResource.isPresent()) {

            LOGGER.debug("Trying to get preferences for fond '{}'.", idFond);

            String prefsUrl = createPrefsUri(fondResource);
            Optional<ExistResource> fondPrefs = momcaConnection.readExistResource(prefsUrl);

            fond = Optional.of(new Fond(fondResource.get(), fondPrefs));

            LOGGER.debug("Read preferences for fond '{}': {}", idFond, fondPrefs);

        }

        LOGGER.info("Returning fond '{}': {}", idFond, fondResource);

        return fond;

    }

    @Override
    public boolean isExisting(@NotNull IdFond idFond) {

        LOGGER.info("Trying to determine the existence of fond '{}'.", idFond);

        boolean isFondExisting = isFondExisting(idFond);

        LOGGER.info("Is fond '{}' existing: {}", idFond, isFondExisting);

        return isFondExisting;

    }

    private boolean isFondExisting(@NotNull IdFond idFond) {

        String uri = createEadResourceUri(idFond);
        return momcaConnection.isResourceExisting(uri);

    }

    @Override
    @NotNull
    public List<IdFond> list(@NotNull IdArchive idArchive) {

        LOGGER.info("Trying to list all fonds belonging to archive '{}'.", idArchive);

        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listFonds(idArchive));
        List<IdFond> fondList = queryResults.stream().map(AtomId::new).map(IdFond::new).collect(Collectors.toList());

        LOGGER.info("Returning '{}' fonds for archive '{}'.", fondList.size(), idArchive);

        return fondList;

    }

}
