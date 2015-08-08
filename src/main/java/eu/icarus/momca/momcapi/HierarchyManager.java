package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Archive;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.eap.Country;
import eu.icarus.momca.momcapi.xml.eap.Subdivision;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by daniel on 20.07.2015.
 */
public class HierarchyManager {

    @NotNull
    private final MomcaConnection momcaConnection;

    public HierarchyManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    @NotNull
    public Optional<Archive> getArchive(@NotNull IdArchive archiveId) {

        List<String> archiveUris = momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(archiveId, null));

        if (archiveUris.size() > 1) {
            String message = String.format("More than one result for archive '%s'", archiveId.getArchiveIdentifier());
            throw new MomcaException(message);
        }

        return archiveUris.isEmpty() ? Optional.empty() : getArchiveFromUri(archiveUris.get(0));

    }

    @NotNull
    public List<IdArchive> listArchives() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listIdArchives());
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdArchive> listArchivesForCountry(@NotNull Country country) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listIdArchivesForCountry(country.getCode()));
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdArchive> listArchivesForSubdivision(@NotNull Subdivision subdivision) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listIdArchivesForSubdivision(subdivision.getNativeform()));
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    private Optional<Archive> getArchiveFromUri(@NotNull String archiveUri) {
        String resourceName = Util.getLastUriPart(archiveUri);
        String parentUri = Util.getParentUri(archiveUri);
        return momcaConnection.getExistResource(resourceName, parentUri).map(Archive::new);
    }

}
