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

        boolean proceed = true;

        if (!isParentExisting(charter)) {
            proceed = false;
            LOGGER.info("The parent fond/collection of charter '{}' is not existing. Aborting addition.", charter.getUri());
        }

        if (proceed && momcaConnection.isResourceExisting(charter.getUri())) {
            proceed = false;
            LOGGER.info("A charter with id '{}' and status '{}'is already existing, aborting addition.", charter.getId(), charter.getCharterStatus());
        }

        boolean success = false;

        if (proceed) {

            String currentTime = momcaConnection.queryRemoteDateTime();
            success = writeCharterToDatabase(charter, currentTime, currentTime);

            if (success) {
                LOGGER.info("Charter '{}' added to the database.", charterUri);
            } else {
                LOGGER.info("Failed to add charter '{}' to the database.", charterUri);
            }

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

        String uri = Charter.createParentUri(id, CharterStatus.PRIVATE, creator);
        String name = Charter.createResourceName(id, CharterStatus.PRIVATE);
        ExistResource resource = new ExistResource(name, uri);

        boolean proceed = true;

        if (!isCharterExisting(id, CharterStatus.PRIVATE.getResourceRoot())) {
            proceed = false;
            LOGGER.info("The private charter '{}' is not existing. Aborting deletion.", id);
        }

        boolean success = false;

        if (proceed) {

            success = momcaConnection.deleteExistResource(resource);

            if (success) {
                LOGGER.info("Private charter '{}' of user '{}' deleted.", id, creator);
            } else {
                LOGGER.info("Failed to delete private charter '{}' of user '{}'", id, creator);
            }

        }

        return success;

    }

    public boolean deletePublicCharter(@NotNull IdCharter id, @NotNull CharterStatus status) {

        LOGGER.info("Trying to delete public charter '{}' with status '{}'", id, status);

        boolean proceed = true;

        if (status == CharterStatus.PRIVATE) {
            proceed = false;
            LOGGER.info("Charter status '{}' is forbidden. Aborting deletion.", status);
        }

        if (proceed && !isCharterExisting(id, status.getResourceRoot())) {
            proceed = false;
            LOGGER.info("Charter with id '{}' and status '{}' is not existing. Aborting deletion.", id, status);
        }

        boolean success = false;

        if (proceed) {

            String uri = Charter.createParentUri(id, status, null);
            String name = Charter.createResourceName(id, status);
            ExistResource resource = new ExistResource(name, uri);

            success = momcaConnection.deleteExistResource(resource);

            if (success) {
                LOGGER.info("Public charter '{}' with status '{}' deleted.", id, status);
            } else {
                LOGGER.info("Failed to delete public charter '{}' with status '{}'.", id, status);
            }

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

    public boolean publishSavedCharter(@NotNull User user, @NotNull IdCharter idCharter) {

        LOGGER.info("Trying to publish the edited charter '{}' from user '{}'.", idCharter, user);

        List<Saved> savedList = user.getSavedCharters();
        List<Saved> withoutCurrent = user.getSavedCharters()
                .stream()
                .filter(saved -> !idCharter.equals(saved.getId()))
                .collect(Collectors.toList());

        boolean proceed = true;

        if (withoutCurrent.size() == savedList.size()) {
            proceed = false;
            LOGGER.info("Charter to be removed (with the id '{}') is not present in the list of chartes currently" +
                    " edited by user '{}'. Aborting publishing.", idCharter, user);
        }

        if (proceed && !isCharterExisting(idCharter, ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED)) {
            proceed = false;
            LOGGER.info("Charter with id '{}' to be published is currently not being edited. Abort publishing.",
                    idCharter);
        }

        boolean success = false;

        if (proceed) {

            ExistQuery query = ExistQueryFactory.publishCharter(idCharter);
            momcaConnection.queryDatabase(query);

            if (isCharterExisting(idCharter, ResourceRoot.PUBLIC_CHARTERS)) {

                user.setSavedCharters(withoutCurrent);
                success = momcaConnection.getUserManager().updateUserData(user);

                if (success) {
                    LOGGER.info("Charter '{}', edited by user '{}' published.", idCharter, user);
                } else {
                    LOGGER.info("Failed to remove charter with id '{}' from the list of edited charters for user" +
                            " '{}'. Needs to be manually removed.", idCharter, user);
                }

            } else {

                LOGGER.info("Failed to update the status of charter '{}' to 'PUBLISHED'. Aborting publishing.", idCharter);

            }

        }

        return success;

    }

    /**
     * Update the CEI content of a charter. The ATOM content, apart from @code{atom:updated} will not be updated
     * (for example @code{atom:author}).
     *
     * @param updatedCharter The charter with updated content.
     * @return true if the update was successful.
     */
    public boolean updateCharterContent(@NotNull Charter updatedCharter) {

        String uri = updatedCharter.getUri();

        LOGGER.info("Trying to update content of charter '{}'.", uri);

        boolean proceed = true;

        if (!isCharterExisting(updatedCharter.getId(), updatedCharter.getCharterStatus().getResourceRoot())) {
            proceed = false;
            LOGGER.info("Charter '{}' is not existing. Aborting update.", uri);
        }

        boolean success = false;

        if (proceed) {

            ExistQuery query = ExistQueryFactory.updateCharterContent(updatedCharter);
            List<String> results = momcaConnection.queryDatabase(query);

            success = results.size() == 1 && results.get(0).equals("0");

            if (success) {
                LOGGER.info("Content of charter '{}' updated.", uri);
            } else {
                LOGGER.info("Content of charter '{}' not successfully updated.", uri);
            }

        }

        return success;

    }

    public boolean updateCharterId(@NotNull IdCharter newId, @NotNull IdCharter originalId,
                                   @NotNull CharterStatus status, @Nullable IdUser creator) {

        LOGGER.info("Trying to update id of charter '{}' with status '{}' and creator '{}' to '{}'.",
                originalId, status, creator, newId);

        ResourceRoot resourceRoot = status.getResourceRoot();

        boolean proceed = true;

        if (originalId.equals(newId)) {
            proceed = false;
            LOGGER.info("The original id '{}' is equal to the new id '{}'. Aborting update.", originalId, newId);
        }

        if (proceed && !isCharterExisting(originalId, resourceRoot)) {
            proceed = false;
            LOGGER.info("There is no charter with id '{}' and status '{}' existing. Aborting update.", originalId, status);
        }

        if (proceed && isCharterExisting(newId, resourceRoot)) {
            proceed = false;
            LOGGER.info("Charter with id '{}' is already existing for new status '{}'. Aborting update.", newId, status);
        }

        if (proceed && status == CharterStatus.PRIVATE && creator == null) {
            proceed = false;
            LOGGER.info("It's not possible to update a private charter without a provided creator. Creator is 'NULL'. Aborting update.");
        }

        if (proceed && !momcaConnection.isCollectionExisting(Charter.createParentUri(newId, status, creator))) {
            proceed = false;
            LOGGER.info("Target hierarchy is not existing for id '{}', status '{}' and creator '{}'. Aborting update.", newId, status, creator);
        }

        boolean success = false;

        if (proceed) {

            String parentUri = Charter.createParentUri(originalId, status, creator);
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

        return success;

    }

    public boolean updateCharterStatus(@NotNull CharterStatus newStatus, @NotNull CharterStatus originalStatus,
                                       @NotNull IdCharter idCharter, @Nullable IdUser creator) {

        LOGGER.info("Trying to update status of charter with id '{}' from '{}' to '{}'", idCharter, originalStatus, newStatus);

        boolean proceed = true;

        if (originalStatus.equals(newStatus)) {
            proceed = false;
            LOGGER.info("The original status '{}' is equal to the new status '{}'. Aborting update.", originalStatus, newStatus);
        }

        if (proceed && !isCharterExisting(idCharter, originalStatus.getResourceRoot())) {
            proceed = false;
            LOGGER.info("There is no charter with id '{}' and status '{}' existing. Aborting update.", idCharter, originalStatus);
        }

        if (proceed && isCharterExisting(idCharter, newStatus.getResourceRoot())) {
            proceed = false;
            LOGGER.info("Charter with id '{}' is already existing for new status '{}'. Aborting update.", idCharter, newStatus);
        }

        if (proceed && (originalStatus == CharterStatus.PRIVATE || newStatus == CharterStatus.PRIVATE) && creator == null) {
            proceed = false;
            LOGGER.info("It's not possible to update to or from status 'PRIVATE' without a provided user. User is 'NULL'. Aborting update.");
        }

        if (proceed && !momcaConnection.isCollectionExisting(Charter.createParentUri(idCharter, newStatus, creator))) {
            proceed = false;
            LOGGER.info("Target hierarchy is not existing for id '{}', status '{}' and creator '{}'. Aborting update.", idCharter, newStatus, creator);
        }

        boolean success = false;

        if (proceed) {

            String oldParentUri = Charter.createParentUri(idCharter, originalStatus, creator);
            String newParentUri = Charter.createParentUri(idCharter, newStatus, creator);
            String oldFileName = Charter.createResourceName(idCharter, originalStatus);
            String newFileName = Charter.createResourceName(idCharter, newStatus);

            ExistQuery query = ExistQueryFactory.moveResource(oldParentUri, newParentUri, newFileName, oldFileName);
            momcaConnection.queryDatabase(query);

            success = (isCharterExisting(idCharter, newStatus.getResourceRoot()) && !isCharterExisting(idCharter, originalStatus.getResourceRoot()));

            if (success) {
                LOGGER.info("Status of charter with id '{}' updated from '{}' to '{}'.", idCharter, originalStatus, newStatus);
            } else {
                LOGGER.info("Failed to update status of charter with id '{}' from '{}' to '{}'.", idCharter, originalStatus, newStatus);
            }

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
