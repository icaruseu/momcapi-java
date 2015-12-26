package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.resource.*;
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

        String charterUri = charter.getUri();
        LOGGER.info("Trying to add charter '{}' to the database.", charterUri);

        boolean success = false;

        if (isParentExisting(charter)) {

            IdCharter id = charter.getId();
            CharterStatus status = charter.getCharterStatus();

            if (momcaConnection.isResourceExisting(charter.getUri())) {

                LOGGER.info("A charter with id '{}' and status '{}'is already existing, aborting addition.", id, status);

            } else {

                String currentTime = momcaConnection.queryRemoteDateTime();
                success = writeCharterToDatabase(charter, currentTime, currentTime);

                if (success) {
                    LOGGER.info("Charter '{}' added to the database.", charterUri);
                } else {
                    LOGGER.info("Failed to add charter '{}' to the database.", charterUri);
                }

            }
        } else {
            LOGGER.info("The parent fond/collection of charter '{}' is not existing. Aborting addition.", charter.getUri());
        }

        return success;

    }

    private List<IdCharter> createIds(List<String> atomIdStrings) {

        return atomIdStrings
                .stream()
                .map(AtomId::new)
                .map(IdCharter::new)
                .collect(Collectors.toList());

    }

    public boolean deletePrivateCharter(@NotNull IdCharter id, @NotNull IdUser creator) {

        LOGGER.info("Trying to delete private charter '{}' of user '{}'", id, creator);

        boolean success;

        String uri = Charter.createParentUri(id, CharterStatus.PRIVATE, creator);
        String name = Charter.createResourceName(id, CharterStatus.PRIVATE);
        ExistResource resource = new ExistResource(name, uri);

        success = momcaConnection.deleteExistResource(resource);

        if (success) {
            LOGGER.info("Private charter '{}' of user '{}' deleted.", id, creator);
        } else {
            LOGGER.info("Failed to delete private charter '{}' of user '{}'", id, creator);
        }

        return success;

    }

    public boolean deletePublicCharter(@NotNull IdCharter id, @NotNull CharterStatus status) {

        LOGGER.info("Trying to delete public charter '{}' with status '{}'", id, status);

        boolean success = false;

        if (status != CharterStatus.PRIVATE) {

            String uri = Charter.createParentUri(id, status, null);
            String name = Charter.createResourceName(id, status);
            ExistResource resource = new ExistResource(name, uri);

            success = momcaConnection.deleteExistResource(resource);

            if (success) {
                LOGGER.info("Public charter '{}' with status '{}' deleted.", id, status);
            } else {
                LOGGER.info("Failed to delete public charter '{}' with status '{}'.", id, status);
            }

        } else {
            LOGGER.info("Charter status '{}' is forbidden. Aborting deletion.", status);
        }

        return success;

    }

    @NotNull
    public Optional<Charter> getCharter(@NotNull IdCharter idCharter, @NotNull CharterStatus charterStatus) {

        LOGGER.info("Trying to get charter '{}' with status '{}' from the database.", idCharter, charterStatus);

        ExistQuery query = ExistQueryFactory.getResourceUri(idCharter.getContentAsElement(), charterStatus.getResourceRoot());
        List<String> results = momcaConnection.queryDatabase(query);

        Optional<Charter> charter;

        if (results.size() > 1) {

            String message = String.format("More than one possible uri for charter '%s' with status '%s' found.",
                    idCharter, charterStatus);
            throw new MomcaException(message);

        } else if (results.size() == 1) {

            charter = getCharterFromUri(results.get(0));

        } else {

            charter = Optional.empty();

        }

        LOGGER.info("Returning '{}' for charter '{}' with status '{}' from the database.", charter, idCharter, charterStatus);

        return charter;

    }

    @NotNull
    private Optional<Charter> getCharterFromUri(@NotNull String charterUri) {
        return momcaConnection.readExistResource(charterUri).map(Charter::new);
    }

    @NotNull
    public List<Charter> getCharterInstances(@NotNull IdCharter idCharter) {

        LOGGER.info("Trying to get all instances for the charter '{}' from the database.", idCharter);

        ExistQuery resourceUri = ExistQueryFactory.getResourceUri(idCharter.getContentAsElement(), null);

        List<Charter> charters = momcaConnection.queryDatabase(resourceUri)
                .stream()
                .map(this::getCharterFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        LOGGER.info("Returning the following instances of the charter '{}' from the database: '{}'", idCharter, charters);

        return charters;

    }

    private boolean isCharterExisting(@NotNull IdCharter idCharter, @Nullable ResourceRoot resourceRoot) {

        LOGGER.trace("Trying to determine the existence of charter '{}' in '{}'.", idCharter, resourceRoot);

        ExistQuery query = ExistQueryFactory.checkAtomResourceExistence(idCharter.getContentAsElement(), resourceRoot);

        boolean isCharterExisting = !momcaConnection.queryDatabase(query).isEmpty();

        LOGGER.trace("Result of query for existence of charter '{}' in '{}': '{}'", idCharter, resourceRoot, isCharterExisting);

        return isCharterExisting;

    }

    private boolean isParentExisting(@NotNull Charter charter) {

        LOGGER.trace("Trying to determine the existence of the parent of charter '{}'.", charter.getId());

        List<String> hierarchicalUriParts = charter.getId().getHierarchicalUriParts();

        boolean isExisting;

        if (charter.isInFond()) {

            String archiveIdentifier = hierarchicalUriParts.get(0);
            String fondIdentifier = hierarchicalUriParts.get(1);
            FondManager fm = momcaConnection.getFondManager();
            IdFond idFond = new IdFond(archiveIdentifier, fondIdentifier);

            isExisting = fm.isFondExisting(idFond);

        } else {

            String identifier = hierarchicalUriParts.get(0);
            CollectionManager cm = momcaConnection.getCollectionManager();
            MyCollectionManager mm = momcaConnection.getMyCollectionManager();
            IdCollection idCollection = new IdCollection(identifier);
            IdMyCollection idMyCollection = new IdMyCollection(identifier);

            isExisting = cm.isCollectionExisting(idCollection) ||
                    mm.isMyCollectionExisting(idMyCollection, MyCollectionStatus.PRIVATE) ||
                    mm.isMyCollectionExisting(idMyCollection, MyCollectionStatus.PUBLISHED);

        }

        LOGGER.trace("Returning the result of the query for the existence of the parent of charter '{}': '{}'",
                charter.getId(), isExisting);

        return isExisting;
    }

    @NotNull
    public List<IdCharter> listChartersInPrivateMyCollection(@NotNull IdMyCollection idMyCollection) {

        LOGGER.info("Trying to list charters in private myCollection '{}'.", idMyCollection);

        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPrivate(idMyCollection));
        List<IdCharter> ids = createIds(queryResults);

        LOGGER.info("Returning the following {} charters for private myCollection '{}': {}", ids.size(), idMyCollection, ids);

        return ids;

    }

    @NotNull
    public List<IdCharter> listImportedCharters(@NotNull IdAtomId idParent) {

        LOGGER.info("Trying to list imported charters for parent '{}'.", idParent);

        List<String> queryResult;

        if (idParent instanceof IdFond) {

            LOGGER.trace("Parent '{}' is an archival fond.", idParent);
            queryResult = momcaConnection.queryDatabase(ExistQueryFactory.listChartersImport((IdFond) idParent));

        } else {

            LOGGER.trace("Parent '{}' is an archival collection.", idParent);
            queryResult = momcaConnection.queryDatabase(ExistQueryFactory.listChartersImport((IdCollection) idParent));

        }

        List<IdCharter> ids = createIds(queryResult);

        LOGGER.info("Returning ids for {} charters belonging to parent '{}': {}", ids.size(), idParent, ids);

        return ids;

    }

    /**
     * Lists charters that are listed in a users file without them being properly saved to metadata.charter.saved.
     * This is due to an old bug in the database.
     *
     * @param idUser the User
     * @return A list of improperly saved charters.
     */
    @NotNull
    public List<IdCharter> listNotExistingSavedCharters(@NotNull IdUser idUser) {

        LOGGER.info("Trying to list saved charters that are not existing in the userspace of user '{}'.", idUser);

        List<IdCharter> results = new ArrayList<>(0);

        momcaConnection.getUserManager().getUser(idUser).ifPresent(user ->
                results.addAll(user.getSavedCharters().stream()
                        .map(Saved::getId)
                        .filter(idCharter -> !isCharterExisting(idCharter, ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED))
                        .collect(Collectors.toList())));

        LOGGER.info("Returning ids for {} saved charters that are not existing in the userspace of user '{}': {}",
                results.size(), idUser, results);

        return results;

    }

    @NotNull
    public List<IdCharter> listPublicCharters(@NotNull IdAtomId idParent) {

        LOGGER.info("Trying to list all public charters belonging to '{}'.", idParent);

        List<String> queryResult;

        if (idParent instanceof IdFond) {

            LOGGER.trace("'{}' is an archival fond.", idParent);
            queryResult = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic((IdFond) idParent));

        } else if (idParent instanceof IdCollection) {

            LOGGER.trace("'{}' is an archival collection.", idParent);
            queryResult = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic((IdCollection) idParent));

        } else {

            LOGGER.trace("'{}' is a myCollection.", idParent);
            queryResult = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPublic((IdMyCollection) idParent));

        }

        List<IdCharter> charters = createIds(queryResult);

        LOGGER.info("Returning ids for {} charters belonging to '{}': {}", charters.size(), idParent, charters);

        return charters;

    }

    @NotNull
    public List<IdCharter> listSavedCharters() {

        LOGGER.info("Trying to list all saved charters.");

        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersSaved());
        List<IdCharter> ids = createIds(queryResults);

        LOGGER.info("Returning ids for {} saved charters: {}", ids.size(), ids);

        return ids;

    }

    @NotNull
    public List<IdCharter> listUsersPrivateCharters(@NotNull IdUser idUser) {

        LOGGER.info("Trying to list all private charters of user '{}'.", idUser);

        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listChartersPrivate(idUser));
        List<IdCharter> ids = createIds(queryResults);

        LOGGER.info("Returning ids of {} private chartes of user '{}: {}'", ids.size(), idUser, ids);

        return ids;

    }

    public void publishSavedCharter(@NotNull User user, @NotNull IdCharter idCharter) {

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
                        idCharter, user.getId());
                throw new MomcaException(message);
            }

            Charter toUpdate = originalCharter.get();
            toUpdate.setCharterStatus(CharterStatus.PUBLIC);

            updateCharter(toUpdate, idCharter, CharterStatus.SAVED);

            user.setSavedCharters(withoutCurrent);

            momcaConnection.getUserManager().updateUserData(user);

        }

    }

    public boolean updateCharter(@NotNull Charter updatedCharter) {

        String uri = updatedCharter.getUri();

        LOGGER.info("Trying to update charter '{}'.", uri);

        boolean success = false;

        if (isCharterExisting(updatedCharter.getId(), updatedCharter.getCharterStatus().getResourceRoot())) {

            ExistQuery query = ExistQueryFactory.updateCharterContent(updatedCharter);
            momcaConnection.queryDatabase(query);

            success = true;

            LOGGER.info("Charter '{}' updated.", uri);

        } else {
            LOGGER.info("Charter '{}' is not existing. Aborting update.", uri);
        }

        return success;

    }

    public boolean updateCharter(@NotNull IdCharter newId, @NotNull IdCharter originalId,
                                 @NotNull CharterStatus status) {

        LOGGER.info("Trying to update id of charter '{}' with status '{}' to '{}'.", originalId, status, newId);

        boolean success = false;

        ResourceRoot resourceRoot = status.getResourceRoot();

        if (originalId.equals(newId)) {

            LOGGER.info("The original id '{}' is equal to the new id '{}'. Aborting update.", originalId, newId);

        } else {

            if (isCharterExisting(originalId, resourceRoot)) {

                if (isCharterExisting(newId, resourceRoot)) {

                    LOGGER.info("Charter with id '{}' is already existing for status '{}'. Aborting update.", newId, status);

                } else {

                    String parentUri = Charter.createParentUri(originalId, status, null);
                    String oldAtomId = originalId.getAtomId();
                    String newAtomId = newId.getAtomId();
                    String newDocumentName = Charter.createResourceName(newId, status);

                    ExistQuery query = ExistQueryFactory.updateCharterAtomId(parentUri, oldAtomId, newAtomId, newDocumentName);
                    momcaConnection.queryDatabase(query);

                    success = isCharterExisting(newId, resourceRoot);

                    if (success) {
                        LOGGER.info("Id of '{}' charter updated from '{}' to '{}'.", status, originalId, newId);
                    } else {
                        LOGGER.info("Failed to update the Id of '{}' charter from '{}' to '{}'.", status, originalId, newId);
                    }

                }

            } else {

                LOGGER.info("There is no charter with id '{}' and status '{}' existing. Aborting update.", originalId, status);

            }

        }

        return success;

    }

    public boolean updateCharter(@NotNull CharterStatus newStatus, @NotNull CharterStatus originalStatus,
                                 @NotNull IdCharter idCharter) {

        // TODO implement

        return false;
    }

    public boolean updateCharter(@NotNull Charter modifiedCharter, @NotNull IdCharter originalId, @Nullable CharterStatus originalStatus) {

        // TODO remove updates of status and id

        boolean success = false;

        LOGGER.info("Trying to update charter '{}' with original id '{}' and original status '{}'.",
                modifiedCharter, originalId, originalStatus);

        IdCharter realOriginalId = originalId == null ? modifiedCharter.getId() : originalId;
        CharterStatus realOriginalStatus = originalStatus == null ? modifiedCharter.getCharterStatus() : originalStatus;

        boolean writeAndDeleteNeccessary = !modifiedCharter.getId().equals(realOriginalId) ||
                modifiedCharter.getCharterStatus() != realOriginalStatus;

        LOGGER.debug("The neccessity to fully write and delete the charter is '{}'.", writeAndDeleteNeccessary);

        if (writeAndDeleteNeccessary) {

            LOGGER.trace("Actual original id of '{}' is '{}''.", modifiedCharter, realOriginalId);
            LOGGER.trace("Actual original status of '{}' is '{}.'", modifiedCharter, realOriginalStatus);

            Optional<Charter> originalCharterOptional = getCharter(realOriginalId, realOriginalStatus);
            if (!originalCharterOptional.isPresent()) {
                LOGGER.info("The charter with id '{}' and status '{}' does not exist in the database.");
            }

            Charter originalCharter = originalCharterOptional.get();

            LOGGER.debug("Removing old version of charter with id '{}' and status '{}'.",
                    realOriginalId, realOriginalStatus);
            momcaConnection.deleteExistResource(originalCharter);
            LOGGER.debug("Old version of the charter with id '{}' and status '{}' removed.",
                    realOriginalId, realOriginalStatus);

            LOGGER.debug("Inserting updated charter '{}'.", modifiedCharter);
            writeCharterToDatabase(modifiedCharter, originalCharter.getPublished(), momcaConnection.queryRemoteDateTime());
            LOGGER.debug("Updated charter '{}' inserted.", modifiedCharter);

        } else {

        }

        return success;

    }

    private boolean writeCharterToDatabase(@NotNull Charter charter, @NotNull String published, @NotNull String updated) {

        String charterUri = charter.getUri();
        LOGGER.trace("Trying to write charter '{}' to the database.", charterUri);

        boolean success = false;

        if (momcaConnection.makeSureCollectionPathExists(charter.getParentUri())) {
            success = momcaConnection.writeAtomResource(charter, published, updated);
        }

        if (success) {
            LOGGER.trace("Charter '{}' written to the database.", charterUri);
        } else {
            LOGGER.trace("Failed to write charter '{}' to the database.", charterUri);
        }

        return success;

    }

}
