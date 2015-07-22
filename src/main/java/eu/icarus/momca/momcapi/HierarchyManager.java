package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Archive;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

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
    private Optional<Archive> getArchiveFromUri(@NotNull String archiveUri) {
        String resourceName = Util.getLastUriPart(archiveUri);
        String parentUri = Util.getParentUri(archiveUri);
        return momcaConnection.getExistResource(resourceName, parentUri).map(Archive::new);
    }

}
