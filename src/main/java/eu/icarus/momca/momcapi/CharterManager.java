package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.query.ExistQuery;
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
    public List<Charter> getCharterInstances(@NotNull AtomIdCharter atomIdCharter) {

        return momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(atomIdCharter, null
        )).stream()
                .map(this::getCharterFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

    @NotNull
    public List<Charter> getCharterInstances(@NotNull AtomIdCharter atomIdCharter, @NotNull CharterStatus charterStatus) {

        return momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(atomIdCharter, charterStatus.getResourceRoot()
        )).stream()
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
        String resourceName = getResourceName(charterUri);
        String parentUri = getParentUri(charterUri);
        return momcaConnection.getExistResource(resourceName, parentUri).map(Charter::new);
    }

    @NotNull
    private String getParentUri(@NotNull String charterUri) {
        return charterUri.substring(0, charterUri.lastIndexOf('/'));
    }

    @NotNull
    private String getResourceName(@NotNull String charterUri) {
        return charterUri.substring(charterUri.lastIndexOf('/') + 1, charterUri.length());
    }

    private boolean isCharterExisting(@NotNull AtomIdCharter atomIdCharter, @Nullable ResourceRoot resourceRoot) {
        ExistQuery query = ExistQueryFactory.checkResourceExistence(atomIdCharter, resourceRoot);
        return !momcaConnection.queryDatabase(query).isEmpty();
    }

}
