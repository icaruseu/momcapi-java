package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Charter;
import eu.icarus.momca.momcapi.resource.CharterStatus;
import eu.icarus.momca.momcapi.resource.ResourceRoot;
import eu.icarus.momca.momcapi.resource.User;
import eu.icarus.momca.momcapi.xml.atom.AtomIdCharter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by daniel on 03.07.2015.
 */
public class CharterManager {

    private final MomcaConnection momcaConnection;

    CharterManager(MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    @NotNull
    public List<Charter> getCharterInstances(@NotNull AtomIdCharter atomIdCharter, @Nullable CharterStatus charterStatus) {

        ResourceRoot root = null;

        if (charterStatus != null) {
            switch (charterStatus) {
                case IMPORTED:
                    root = ResourceRoot.METADATA_CHARTER_IMPORT;
                    break;
                case PRIVATE:
                    root = ResourceRoot.XRX_USER;
                    break;
                case PUBLIC:
                    root = ResourceRoot.METADATA_CHARTER_PUBLIC;
                    break;
                case SAVED:
                    root = ResourceRoot.METADATA_CHARTER_SAVED;
                    break;
            }
        }

        return momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(atomIdCharter, root)).stream()
                .map(this::getCharterFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

    @NotNull
    public List<AtomIdCharter> listErroneouslySavedCharters(@NotNull User user) {
        return user.listSavedCharterIds().stream()
                .filter(charterAtomId -> !isCharterExisting(charterAtomId, ResourceRoot.METADATA_CHARTER_SAVED))
                .collect(Collectors.toList());
    }

    @NotNull
    private Optional<Charter> getCharterFromUri(@NotNull String charterUri) {
        String resourceName = charterUri.substring(charterUri.lastIndexOf('/') + 1, charterUri.length());
        String parentUri = charterUri.substring(0, charterUri.lastIndexOf('/'));
        return momcaConnection.getExistResource(resourceName, parentUri).map(Charter::new);
    }

    private boolean isCharterExisting(@NotNull AtomIdCharter atomIdCharter, @Nullable ResourceRoot resourceRoot) {
        return !momcaConnection.queryDatabase(ExistQueryFactory.checkResourceExistence(atomIdCharter, resourceRoot)).isEmpty();
    }

}
