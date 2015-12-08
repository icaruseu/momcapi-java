package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.resource.Archive;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
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

        boolean success = false;
        String identifier = newArchive.getIdentifier();

        LOGGER.info("Try to add archive '{}' to the database.", identifier);

        if (getArchive(newArchive.getId()).isPresent()) {

            LOGGER.info("Archive '{}' already exists, aborting addition.", identifier);

        } else {

            boolean isRegionOk = newArchive.getRegionName()
                    .map(s -> momcaConnection.getCountryManager()
                            .getRegions(newArchive.getCountry())
                            .stream()
                            .anyMatch(region -> s.equals(region.getNativeName())))
                    .orElse(true);

            if (isRegionOk) {

                momcaConnection.createCollection(identifier, ResourceRoot.ARCHIVES.getUri());
                String time = momcaConnection.queryRemoteDateTime();
                momcaConnection.writeAtomResource(newArchive, time, time);

                success = true;
                LOGGER.info("Archive '{}' added.", identifier);

            } else {

                LOGGER.info("The region of the archive to be added ({}) is not part of '{}' in the database," +
                                " aborting addition",
                        newArchive.getRegionName().get(),
                        newArchive.getCountry().getNativeName());

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

    public boolean deleteArchive(@NotNull IdArchive idArchive) {

        boolean success = false;
        String identifier = idArchive.getIdentifier();

        LOGGER.info("Trying to delete archive '{}'", identifier);

        boolean stillHasFonds = !momcaConnection.getFondManager().listFonds(idArchive).isEmpty();

        if (stillHasFonds) {

            LOGGER.info("The archive '{}',  that is to be deleted still has associated fonds, aborting deletion.",
                    identifier);

        } else {

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

        Optional<Archive> archive = getFirstMatchingExistResource(idArchive.getContentAsElement()).map(Archive::new);

        LOGGER.info("Returning '{}' for archive '{}'.", archive, identifier);

        return archive;

    }

    @NotNull
    public List<IdArchive> listArchives() {

        LOGGER.info("Trying to list all archives in the database.");

        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listArchives());

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

        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listArchivesForRegion(nativeName));

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

        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listArchivesForCountry(country.getCountryCode()));

        List<IdArchive> archiveList = collectArchiveIds(queryResults);

        int resultSize = archiveList.size();
        LOGGER.info("Returning {} {} for country '{}'.",
                resultSize,
                resultSize == 1 ? "archive" : "archives",
                nativeName);

        return archiveList;

    }

}
