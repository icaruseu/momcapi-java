package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.resource.Charter;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.model.xml.xrx.Saved;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    public void addCharter(@NotNull Charter charter) {

        IdCharter id = charter.getId();
        CharterStatus status = charter.getCharterStatus();

        if (getCharter(id, status).isPresent()) {
            throw new MomcaException(String.format("A charter with id '%s' and status '%s'is already existing.", id, status));
        }

        if (isParentExisting(id)) {

            momcaConnection.createCollectionPath(charter.getParentUri());
            String time = momcaConnection.getRemoteDateTime();
            momcaConnection.storeAtomResource(charter, time, time);

        } else {
            String message = String.format("The parent for the charter, '%s', is not existing.",
                    id.getHierarchicalUriPartsAsString());
            throw new MomcaException(message);
        }

    }

    public void delete(@NotNull IdCharter id, @NotNull CharterStatus status) {
        getCharter(id, status).ifPresent(momcaConnection::deleteExistResource);
    }

    @NotNull
    public Optional<Charter> getCharter(@NotNull IdCharter idCharter, @NotNull CharterStatus charterStatus) {

        List<String> uriResults = momcaConnection.queryDatabase(
                ExistQueryFactory.getResourceUri(idCharter.getContentXml(), charterStatus.getResourceRoot()));

        Optional<Charter> charter;

        if (uriResults.size() > 1) {
            String message = String.format("More than one possible uri for charter '%s' with status '%s' found.",
                    idCharter, charterStatus);
            throw new MomcaException(message);
        } else if (uriResults.size() == 1) {
            charter = getCharterFromUri(uriResults.get(0));
        } else {
            charter = Optional.empty();
        }

        return charter;

    }

    @NotNull
    private Optional<Charter> getCharterFromUri(@NotNull String charterUri) {
        String resourceName = Util.getLastUriPart(charterUri);
        String parentUri = Util.getParentUri(charterUri);
        return momcaConnection.getExistResource(resourceName, parentUri).map(Charter::new);
    }

    @NotNull
    public List<Charter> getCharterInstances(@NotNull IdCharter idCharter) {

        return momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(idCharter.getContentXml(), null
        )).stream()
                .map(this::getCharterFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

    private boolean isCharterExisting(@NotNull IdCharter idCharter, @Nullable ResourceRoot resourceRoot) {
        ExistQuery query = ExistQueryFactory.checkResourceExistence(idCharter.getContentXml(), resourceRoot);
        return !momcaConnection.queryDatabase(query).isEmpty();
    }

    private boolean isParentExisting(IdCharter id) {

        boolean parentExists = false;

        List<String> uriParts = id.getHierarchicalUriParts();

        switch (uriParts.size()) {

            case 1:
                IdCollection idCollection = new IdCollection(uriParts.get(0));
                IdMyCollection idMyCollection = new IdMyCollection(uriParts.get(0));
                parentExists = momcaConnection.getCollectionManager().getCollection(idCollection).isPresent() ||
                        momcaConnection.getMyCollectionManager().getMyCollection(idMyCollection, MyCollectionStatus.PRIVATE).isPresent();
                break;

            case 2:
                IdFond idFond = new IdFond(uriParts.get(0), uriParts.get(1));
                parentExists = momcaConnection.getFondManager().getFond(idFond).isPresent();
                break;

        }

        return parentExists;

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
    public List<IdCharter> listChartersPrivate(@NotNull IdUser idUser) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPrivate(idUser));
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
    public List<IdCharter> listErroneouslySavedCharters(@NotNull IdUser idUser) {

        List<IdCharter> results = new ArrayList<>(0);

        momcaConnection.getUserManager().getUser(idUser).ifPresent(user ->
                results.addAll(user.getSavedCharters().stream()
                        .map(Saved::getId)
                        .filter(idCharter -> !isCharterExisting(idCharter, ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED))
                        .collect(Collectors.toList())));

        return results;

    }

    public void updateCharter(@NotNull Charter modifiedCharter, @Nullable IdCharter originalId, @Nullable CharterStatus originalStatus) {

        IdCharter realOriginalId = originalId == null ? modifiedCharter.getId() : originalId;
        CharterStatus realOriginalStatus = originalStatus == null ? modifiedCharter.getCharterStatus() : originalStatus;

        if (!getCharter(realOriginalId, realOriginalStatus).isPresent()) {
            throw new MomcaException("The charter to be updated doesn't exist in the database.");
        }

        delete(realOriginalId, realOriginalStatus);

        if (getCharter(modifiedCharter.getId(), modifiedCharter.getCharterStatus()).isPresent()) {
            // delete already existing changed charter in case of an overwrite by moving
            delete(modifiedCharter.getId(), modifiedCharter.getCharterStatus());
        }

        addCharter(modifiedCharter);

    }

}
