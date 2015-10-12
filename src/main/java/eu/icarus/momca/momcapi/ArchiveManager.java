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

                momcaConnection.writeCollection(identifier, ResourceRoot.ARCHIVES.getUri());
                String time = momcaConnection.queryRemoteDateTime();
                momcaConnection.writeAtomResource(newArchive, time, time);

                success = true;
                LOGGER.info("Archive '{}' added.", identifier);

            } else {

                LOGGER.info("The region of the archive to be added ({}) is not part of '{}' in the database, aborting addition",
                        newArchive.getRegionName().get(),
                        newArchive.getCountry().getNativeName());

            }

        }

        return success;

    }

    public void deleteArchive(@NotNull IdArchive idArchive) {

        if (!momcaConnection.getFondManager().listFonds(idArchive).isEmpty()) {
            String message = String.format("The archive '%s',  that is to be deleted still has associated fonds.",
                    idArchive.getIdentifier());
            throw new IllegalArgumentException(message);
        }

        momcaConnection.deleteCollection(String.format("%s/%s", ResourceRoot.ARCHIVES.getUri(),
                idArchive.getIdentifier()));
        momcaConnection.deleteCollection(String.format("%s/%s",
                ResourceRoot.ARCHIVAL_FONDS.getUri(), idArchive.getIdentifier()));

    }

    @NotNull
    public Optional<Archive> getArchive(@NotNull IdArchive idArchive) {
        return getExistResource(idArchive.getContentXml()).map(Archive::new);
    }

    @NotNull
    public List<IdArchive> listArchives(@NotNull Region region) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listArchivesForRegion(region.getNativeName()));
        return queryResults.stream().map(AtomId::new).map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdArchive> listArchives() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listArchives());
        return queryResults.stream().map(AtomId::new).map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdArchive> listArchives(@NotNull Country country) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listArchivesForCountry(country.getCountryCode()));
        return queryResults.stream().map(AtomId::new).map(IdArchive::new).collect(Collectors.toList());
    }

}
