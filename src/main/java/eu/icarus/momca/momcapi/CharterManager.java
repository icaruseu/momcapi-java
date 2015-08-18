package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Charter;
import eu.icarus.momca.momcapi.resource.CharterStatus;
import eu.icarus.momca.momcapi.resource.ResourceRoot;
import eu.icarus.momca.momcapi.resource.User;
import eu.icarus.momca.momcapi.xml.atom.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Created by daniel on 03.07.2015.
 */
public class CharterManager extends AbstractManager {

    CharterManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @NotNull
    public List<Charter> getCharterInstances(@NotNull IdCharter idCharter) {

        return momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(idCharter.getAtomId(), null
        )).stream()
                .map(this::getCharterFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

    @NotNull
    public List<Charter> getCharterInstances(@NotNull IdCharter idCharter, @NotNull CharterStatus charterStatus) {

        return momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(idCharter.getAtomId(), charterStatus.getResourceRoot()
        )).stream()
                .map(this::getCharterFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

    @NotNull
    public List<IdCharter> listChartersImport(@NotNull IdFond idFond) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersImport(idFond));
        return queryResults.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCharter> listChartersImport(@NotNull IdCollection idCollection) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersImport(idCollection));
        return queryResults.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCharter> listChartersPrivate(@NotNull IdMyCollection idMyCollection) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPrivate(idMyCollection));
        return queryResults.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCharter> listChartersPrivate(@NotNull User user) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPrivate(user));
        return queryResults.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCharter> listChartersPublic(@NotNull IdFond idFond) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic(idFond));
        return queryResults.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCharter> listChartersPublic(@NotNull IdCollection idCollection) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic(idCollection));
        return queryResults.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCharter> listChartersPublic(@NotNull IdMyCollection idMyCollection) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic(idMyCollection));
        return queryResults.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCharter> listChartersSaved() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersSaved());
        return queryResults.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCharter> listErroneouslySavedCharters(@NotNull User user) {
        return user.listSavedCharterIds().stream()
                .filter(idCharter -> !isCharterExisting(idCharter, ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED))
                .collect(Collectors.toList());
    }

    @NotNull
    private Optional<Charter> getCharterFromUri(@NotNull String charterUri) {
        String resourceName = Util.getLastUriPart(charterUri);
        String parentUri = Util.getParentUri(charterUri);
        return momcaConnection.getExistResource(resourceName, parentUri).map(Charter::new);
    }

    private boolean isCharterExisting(@NotNull IdCharter idCharter, @Nullable ResourceRoot resourceRoot) {
        ExistQuery query = ExistQueryFactory.checkResourceExistence(idCharter.getAtomId(), resourceRoot);
        return !momcaConnection.queryDatabase(query).isEmpty();
    }

}
