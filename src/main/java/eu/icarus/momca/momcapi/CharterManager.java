package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.resource.Charter;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.resource.User;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.model.xml.xrx.Saved;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Created by daniel on 03.07.2015.
 */
public class CharterManager extends AbstractManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharterManager.class);

    CharterManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    public boolean addCharter(@NotNull Charter charter) {

        boolean success = false;
        String atomIdText = charter.getId().getAtomIdText();
        LOGGER.info("Trying to add charter '{}' to the database.", atomIdText);

        IdCharter id = charter.getId();
        CharterStatus status = charter.getCharterStatus();

        if (getCharter(id, status).isPresent()) {

            LOGGER.info("A charter with id '{}' and status '{}'is already existing, aborting addition.", id, status);

        } else {

            String currentTime = momcaConnection.queryRemoteDateTime();
            success = writeCharterToDatabase(charter, currentTime, currentTime);

            if (success) {
                LOGGER.info("Charter '{}' added to the database.", atomIdText);
            } else {
                LOGGER.info("Failed to add charter '{}' to the database.", atomIdText);
            }

        }

        return success;

    }

    private List<IdCharter> createIds(List<String> atomIdStrings) {
        return atomIdStrings.stream().map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    public void deleteCharter(@NotNull IdCharter id, @NotNull CharterStatus status) {
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
        return momcaConnection.readExistResource(resourceName, parentUri).map(Charter::new);
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
        ExistQuery query = ExistQueryFactory.checkAtomResourceExistence(idCharter.getContentXml(), resourceRoot);
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
        return createIds(queryResults);
    }

    @NotNull
    public List<IdCharter> listChartersImport(@NotNull IdCollection idCollection) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersImport(idCollection));
        return createIds(queryResults);
    }

    @NotNull
    public List<IdCharter> listChartersPrivate(@NotNull IdMyCollection idMyCollection) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPrivate(idMyCollection));
        return createIds(queryResults);
    }

    @NotNull
    public List<IdCharter> listChartersPrivate(@NotNull IdUser idUser) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPrivate(idUser));
        return createIds(queryResults);
    }

    @NotNull
    public List<IdCharter> listChartersPublic(@NotNull IdFond idFond) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic(idFond));
        return createIds(queryResults);
    }

    @NotNull
    public List<IdCharter> listChartersPublic(@NotNull IdCollection idCollection) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic(idCollection));
        return createIds(queryResults);
    }

    @NotNull
    public List<IdCharter> listChartersPublic(@NotNull IdMyCollection idMyCollection) {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic(idMyCollection));
        return createIds(queryResults);
    }

    @NotNull
    public List<IdCharter> listChartersSaved() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersSaved());
        return createIds(queryResults);
    }

    /**
     * Lists charters that are listed in a users file without them being properly saved to metadata.charter.saved.
     * This is due to an old bug in the database.
     *
     * @param idUser the User
     * @return A list of improperly saved charters.
     */
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

    public void publishCharter(@NotNull IdUser idUser, @NotNull IdCharter idCharter) {

        UserManager userManager = momcaConnection.getUserManager();
        Optional<User> userOptional = userManager.getUser(idUser);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("User '" + idUser + "' not existing.");
        }

        User user = userOptional.get();

        List<Saved> savedList = user.getSavedCharters();
        List<Saved> withoutCurrent = user.getSavedCharters()
                .stream()
                .filter(saved -> !idCharter.equals(saved.getId()))
                .collect(Collectors.toList());

        if (withoutCurrent.size() == savedList.size() - 1) {

            Optional<Charter> originalCharter = getCharter(idCharter, CharterStatus.SAVED);

            if (!originalCharter.isPresent()) {
                String message = String.format(
                        "The charter with the id '%s'to be published for user '%s' is not existing in 'metadata.charter.saved'.",
                        idCharter, idUser);
                throw new MomcaException(message);
            }

            Charter toUpdate = originalCharter.get();
            toUpdate.setCharterStatus(CharterStatus.PUBLIC);

            updateCharter(toUpdate, idCharter, CharterStatus.SAVED);

            user.setSavedCharters(withoutCurrent);

            userManager.updateUserData(user);

        }

    }

    public void updateCharter(@NotNull Charter modifiedCharter, @Nullable IdCharter originalId, @Nullable CharterStatus originalStatus) {

        IdCharter realOriginalId = originalId == null ? modifiedCharter.getId() : originalId;
        CharterStatus realOriginalStatus = originalStatus == null ? modifiedCharter.getCharterStatus() : originalStatus;

        Optional<Charter> originalCharterOptional = getCharter(realOriginalId, realOriginalStatus);
        if (!originalCharterOptional.isPresent()) {
            throw new MomcaException("The charter to be updated doesn't exist in the database.");
        }

        Charter originalCharter = originalCharterOptional.get();

        deleteCharter(originalCharter.getId(), originalCharter.getCharterStatus());

        writeCharterToDatabase(modifiedCharter, originalCharter.getPublished(), momcaConnection.queryRemoteDateTime());

    }

    private boolean writeCharterToDatabase(@NotNull Charter charter, @NotNull String published,
                                           @NotNull String updated) {
        boolean success = false;
        String atomIdText = charter.getId().getAtomIdText();
        LOGGER.debug("Trying to write charter '{}' to the database.", atomIdText);

        if (isParentExisting(charter.getId())) {

            String parentUri = charter.getParentUri();
            success = momcaConnection.createCollectionPath(parentUri);

            if (success) {

                success = momcaConnection.writeAtomResource(charter, published, updated);

                if (success) {
                    LOGGER.debug("Charter '{}' written to the database.", atomIdText);
                } else {
                    LOGGER.debug("Failed to write charter '{}' into newly created collection '{}'.",
                            atomIdText, parentUri);
                }

            } else {

                LOGGER.debug("Failed to create collection path '{}', aborting write.", parentUri);

            }


        } else {
            LOGGER.debug("The parent for the charter, '{}', is not existing, aborting write.",
                    charter.getId().getHierarchicalUriPartsAsString());
        }

        return success;

    }

}
