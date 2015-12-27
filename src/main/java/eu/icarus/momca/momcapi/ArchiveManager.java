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

    public boolean addArchive(@NotNull Archive newArchive) {

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

    private List<IdArchive> collectArchiveIds(List<String> queryResults) {

        return queryResults
                .stream()
                .map(AtomId::new)
                .map(IdArchive::new)
                .collect(Collectors.toList());

    }

    private String createUriFromArchiveId(IdArchive idArchive) {

        return String.format(
                "%s/%s/%s%s",
                ResourceRoot.ARCHIVES.getUri(),
                idArchive.getIdentifier(),
                idArchive.getIdentifier(),
                ResourceType.ARCHIVE.getNameSuffix());

    }

    public boolean deleteArchive(@NotNull IdArchive idArchive) {

        String identifier = idArchive.getIdentifier();

        LOGGER.info("Trying to delete archive '{}'", identifier);

        boolean proceed = true;

        if (!isArchiveExisting(idArchive)) {
            proceed = false;
            LOGGER.info("The archive '{}' that is to be deleted doesn't exist. Aborting deletion.", identifier);
        }

        if (proceed && !momcaConnection.getFondManager().listFonds(idArchive).isEmpty()) {
            proceed = false;
            LOGGER.info("The archive '{}' that is to be deleted still has associated fonds. Aborting deletion.", identifier);
        }

        boolean success = false;

        if (proceed) {

            String archiveCollectionUri = String.format("%s/%s", ResourceRoot.ARCHIVES.getUri(), identifier);
            LOGGER.trace("Trying to delete archival collection at '{}'", archiveCollectionUri);

            success = momcaConnection.deleteCollection(archiveCollectionUri);

            if (success) {

                String archiveFondsCollectionUri = String.format("%s/%s", ResourceRoot.ARCHIVAL_FONDS.getUri(), identifier);
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
    public Optional<Archive> getArchive(@NotNull IdArchive idArchive) {

        String identifier = idArchive.getIdentifier();

        LOGGER.info("Trying to get archive '{}'.", identifier);

        String uri = createUriFromArchiveId(idArchive);

        Optional<Archive> archive = momcaConnection.readExistResource(uri).map(Archive::new);

        LOGGER.info("Returning '{}' for archive '{}'.", archive, identifier);

        return archive;

    }

    private boolean isArchiveExisting(IdArchive idArchive) {

        LOGGER.debug("Try to determine the existance of archive '{}'.", idArchive);

        String uri = createUriFromArchiveId(idArchive);

        ExistQuery query = ExistQueryFactory.checkExistResourceExistence(uri);
        List<String> results = momcaConnection.queryDatabase(query);

        boolean isArchiveExisting = results.size() == 1 && results.get(0).equals("true");

        LOGGER.debug("The result for the query for existence of archive '{}' is '{}'", idArchive, isArchiveExisting);

        return isArchiveExisting;

    }

    @NotNull
    public List<IdArchive> listArchives() {

        LOGGER.info("Trying to list all archives in the database.");

        ExistQuery query = ExistQueryFactory.listArchives();
        List<String> queryResults = momcaConnection.queryDatabase(query);

        List<IdArchive> archiveList = collectArchiveIds(queryResults);

        int resultSize = archiveList.size();
        LOGGER.info("Returning {} {}.",
                resultSize,
                resultSize == 1 ? "archive" : "archives");

        return archiveList;

    }

    @NotNull
    public List<IdArchive> listArchives(@NotNull Region region) {

        String nativeName = region.getNativeName();

        LOGGER.info("Trying to list all archives for region '{}'.", nativeName);

        ExistQuery query = ExistQueryFactory.listArchivesForRegion(nativeName);
        List<String> queryResults = momcaConnection.queryDatabase(query);

        List<IdArchive> archiveList = collectArchiveIds(queryResults);

        int resultSize = archiveList.size();
        LOGGER.info("Returning {} {} for region '{}'.",
                resultSize,
                resultSize == 1 ? "archive" : "archives",
                nativeName);

        return archiveList;

    }

    @NotNull
    public List<IdArchive> listArchives(@NotNull Country country) {

        String nativeName = country.getNativeName();
        LOGGER.info("Trying to list all archives for country '{}'.", nativeName);

        ExistQuery query = ExistQueryFactory.listArchivesForCountry(country.getCountryCode());
        List<String> queryResults = momcaConnection.queryDatabase(query);

        List<IdArchive> archiveList = collectArchiveIds(queryResults);

        int resultSize = archiveList.size();
        LOGGER.info("Returning {} {} for country '{}'.",
                resultSize,
                resultSize == 1 ? "archive" : "archives",
                nativeName);

        return archiveList;

    }

}
