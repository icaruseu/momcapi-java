package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.resource.Archive;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of <code>ArchiveManager</code> based on an eXist MOM-CA connection.
 */
class ExistArchiveManager extends AbstractExistManager implements ArchiveManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistArchiveManager.class);

    /**
     * Creates an archive manager instance.
     *
     * @param momcaConnection The MOM-CA connection.
     */
    ExistArchiveManager(@NotNull ExistMomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @Override
    public boolean add(@NotNull Archive archive) {

        String identifier = archive.getIdentifier();

        LOGGER.info("Try to add archive '{}' to the database.", identifier);

        boolean proceed = true;

        if (isArchiveExisting(archive.getId())) {
            proceed = false;
            LOGGER.info("Archive '{}' already exists, aborting addition.", identifier);
        }

        if (proceed && archive.getRegionName().isPresent() &&
                !momcaConnection.getCountryManager().isRegionExisting(archive.getCountry(), archive.getRegionName().get())) {
            proceed = false;
            LOGGER.info("Region of archive to be added ({}) is not part of '{}' in the database. Aborting addition",
                    archive.getRegionName().get(),
                    archive.getCountry().getNativeName());
        }

        boolean success = false;

        if (proceed) {

            momcaConnection.createCollection(identifier, ResourceRoot.ARCHIVES.getUri());
            String time = momcaConnection.queryRemoteDateTime();
            success = momcaConnection.writeAtomResource(archive, time, time);

            if (success) {
                LOGGER.info("Archive '{}' added.", identifier);
            } else {
                LOGGER.info("Archive '{}' not added.", identifier);
            }

        }

        return success;

    }

    @NotNull
    private String createCollectionUri(@NotNull String identifier, @NotNull String uri) {
        return String.format("%s/%s", uri, identifier);
    }

    @NotNull
    private String createResourceUri(@NotNull IdArchive idArchive) {

        return String.format(
                "%s/%s/%s%s",
                ResourceRoot.ARCHIVES.getUri(),
                idArchive.getIdentifier(),
                idArchive.getIdentifier(),
                ResourceType.ARCHIVE.getNameSuffix());

    }

    @Override
    public boolean delete(@NotNull IdArchive id) {

        String identifier = id.getIdentifier();

        LOGGER.info("Trying to delete archive '{}'", identifier);

        boolean proceed = true;

        if (!isArchiveExisting(id)) {
            proceed = false;
            LOGGER.info("The archive '{}' that is to be deleted doesn't exist. Aborting deletion.", identifier);
        }

        if (proceed && !momcaConnection.getFondManager().list(id).isEmpty()) {
            proceed = false;
            LOGGER.info("The archive '{}' that is to be deleted still has associated fonds. Aborting deletion.", identifier);
        }

        boolean success = false;

        if (proceed) {

            String archiveCollectionUri = createCollectionUri(identifier, ResourceRoot.ARCHIVES.getUri());
            success = momcaConnection.deleteCollection(archiveCollectionUri);

            if (success) {

                String archiveFondsCollectionUri = createCollectionUri(identifier, ResourceRoot.ARCHIVAL_FONDS.getUri());
                success = momcaConnection.deleteCollection(archiveFondsCollectionUri);

                if (success) {
                    LOGGER.info("Deleted archive '{}'.", identifier);
                } else {
                    LOGGER.info("Deleted archive '{}' but failed to delete archival fonds collection at '{}'.",
                            identifier, archiveFondsCollectionUri);
                    success = true;
                }

            } else {
                LOGGER.info("Failed to delete archival collection at '{}', aborting deletion.", archiveCollectionUri);
            }

        }

        return success;

    }

    @Override
    @NotNull
    public Optional<Archive> get(@NotNull IdArchive id) {

        String identifier = id.getIdentifier();

        LOGGER.info("Trying to get archive '{}'.", identifier);

        String uri = createResourceUri(id);
        Optional<Archive> archive = momcaConnection.readExistResource(uri).map(Archive::new);

        LOGGER.info("Returning '{}' for archive '{}'.", archive, identifier);

        return archive;

    }

    private boolean isArchiveExisting(@NotNull IdArchive idArchive) {

        String uri = createResourceUri(idArchive);
        return momcaConnection.isResourceExisting(uri);

    }

    @Override
    public boolean isExisting(@NotNull IdArchive id) {

        LOGGER.info("Try to determine the existance of archive '{}'.", id);

        boolean isArchiveExisting = isArchiveExisting(id);

        LOGGER.info("The result for the query for existence of archive '{}' is '{}'", id, isArchiveExisting);

        return isArchiveExisting;

    }

    @Override
    @NotNull
    public List<IdArchive> list() {

        LOGGER.info("Trying to list all archives in the database.");

        ExistQuery query = ExistQueryFactory.listArchives();
        List<IdArchive> archiveList = queryIdList(query);

        int resultSize = archiveList.size();
        LOGGER.info("Returning {} {}.",
                resultSize,
                resultSize == 1 ? "archive" : "archives");

        return archiveList;

    }

    @Override
    @NotNull
    public List<IdArchive> list(@NotNull Region region) {

        String nativeName = region.getNativeName();

        LOGGER.info("Trying to list all archives for region '{}'.", nativeName);

        ExistQuery query = ExistQueryFactory.listArchivesForRegion(nativeName);
        List<IdArchive> archiveList = queryIdList(query);

        int resultSize = archiveList.size();
        LOGGER.info("Returning {} {} for region '{}'.",
                resultSize,
                resultSize == 1 ? "archive" : "archives",
                nativeName);

        return archiveList;

    }

    @Override
    @NotNull
    public List<IdArchive> list(@NotNull Country country) {

        String nativeName = country.getNativeName();
        LOGGER.info("Trying to list all archives for country '{}'.", nativeName);

        ExistQuery query = ExistQueryFactory.listArchivesForCountry(country.getCountryCode());
        List<IdArchive> archiveList = queryIdList(query);

        int resultSize = archiveList.size();
        LOGGER.info("Returning {} {} for country '{}'.",
                resultSize,
                resultSize == 1 ? "archive" : "archives",
                nativeName);

        return archiveList;

    }

    private List<IdArchive> queryIdList(@NotNull ExistQuery query) {

        return momcaConnection
                .queryDatabase(query)
                .stream()
                .map(AtomId::new)
                .map(IdArchive::new)
                .collect(Collectors.toList());

    }

}
