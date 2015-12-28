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
 * Created by daniel on 20.07.2015.
 */
public class ArchiveManager extends AbstractManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveManager.class);

    ArchiveManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    public boolean add(@NotNull Archive newArchive) {

        String identifier = newArchive.getIdentifier();

        LOGGER.info("Try to add archive '{}' to the database.", identifier);

        boolean proceed = true;

        if (isArchiveExisting(newArchive.getId())) {
            proceed = false;
            LOGGER.info("Archive '{}' already exists, aborting addition.", identifier);
        }

        if (proceed && newArchive.getRegionName().isPresent() &&
                !momcaConnection.getCountryManager().isRegionExisting(newArchive.getCountry(), newArchive.getRegionName().get())) {
            proceed = false;
            LOGGER.info("Region of archive to be added ({}) is not part of '{}' in the database. Aborting addition",
                    newArchive.getRegionName().get(),
                    newArchive.getCountry().getNativeName());
        }

        boolean success = false;

        if (proceed) {

            momcaConnection.createCollection(identifier, ResourceRoot.ARCHIVES.getUri());
            String time = momcaConnection.queryRemoteDateTime();
            success = momcaConnection.writeAtomResource(newArchive, time, time);

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

    /**
     * Deltes an archive from the database. The archive is not allowed to still have existing fonds.
     *
     * @param idArchive The archive to delete.
     * @return True if the process was successful. Note: Returns true, even if the deletion of any empty fond
     * eXist-collections didn't succeed.
     */
    public boolean delete(@NotNull IdArchive idArchive) {

        String identifier = idArchive.getIdentifier();

        LOGGER.info("Trying to delete archive '{}'", identifier);

        boolean proceed = true;

        if (!isArchiveExisting(idArchive)) {
            proceed = false;
            LOGGER.info("The archive '{}' that is to be deleted doesn't exist. Aborting deletion.", identifier);
        }

        if (proceed && !momcaConnection.getFondManager().list(idArchive).isEmpty()) {
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

    @NotNull
    public Optional<Archive> get(@NotNull IdArchive idArchive) {

        String identifier = idArchive.getIdentifier();

        LOGGER.info("Trying to get archive '{}'.", identifier);

        String uri = createResourceUri(idArchive);
        Optional<Archive> archive = momcaConnection.readExistResource(uri).map(Archive::new);

        LOGGER.info("Returning '{}' for archive '{}'.", archive, identifier);

        return archive;

    }

    private boolean isArchiveExisting(@NotNull IdArchive idArchive) {

        String uri = createResourceUri(idArchive);
        return momcaConnection.isResourceExisting(uri);

    }

    public boolean isExisting(@NotNull IdArchive idArchive) {

        LOGGER.info("Try to determine the existance of archive '{}'.", idArchive);

        boolean isArchiveExisting = isArchiveExisting(idArchive);

        LOGGER.info("The result for the query for existence of archive '{}' is '{}'", idArchive, isArchiveExisting);

        return isArchiveExisting;

    }

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
