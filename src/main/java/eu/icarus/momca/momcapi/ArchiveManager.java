package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.resource.Archive;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by daniel on 20.07.2015.
 */
public class ArchiveManager extends AbstractManager {

    ArchiveManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    public void addArchive(@NotNull Archive newArchive) {

        if (getArchive(newArchive.getId()).isPresent()) {
            String message = String.format("The archive '%s' that is to be added already exists.",
                    newArchive.getId().getIdentifier());
            throw new IllegalArgumentException(message);
        }

        newArchive.getRegionName().ifPresent(s -> {

            if (momcaConnection.getCountryManager().getRegions(newArchive.getCountry())
                    .stream().noneMatch(region -> s.equals(region.getNativeName()))
                    ) {

                String message = String.format("The region of the archive ('%s') to be added is not part of the " +
                                "archive's country ('%s') in the database.",
                        newArchive.getRegionName(),
                        newArchive.getCountry().getNativeName());
                throw new MomcaException(message);

            }

        });

        momcaConnection.writeCollection(newArchive.getIdentifier(), ResourceRoot.ARCHIVES.getUri());
        String time = momcaConnection.queryRemoteDateTime();
        momcaConnection.writeAtomResource(newArchive, time, time);

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
