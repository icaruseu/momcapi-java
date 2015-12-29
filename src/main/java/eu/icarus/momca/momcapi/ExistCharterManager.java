package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.resource.Charter;
import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
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
public class ExistCharterManager extends AbstractExistManager implements CharterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistCharterManager.class);

    ExistCharterManager(@NotNull ExistMomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @Override
    public boolean add(@NotNull Charter charter) {

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

    @NotNull
    private List<IdCharter> createIds(@NotNull List<String> atomIdStrings) {

        return atomIdStrings
                .stream()
                .map(AtomId::new)
                .map(IdCharter::new)
                .collect(Collectors.toList());

    }

    @Override
    public boolean delete(@NotNull IdCharter id) {
        return delete(id, CharterStatus.PUBLIC, null);
    }

    @Override
    public boolean delete(@NotNull IdCharter id, @NotNull CharterStatus status, @Nullable IdUser author) {

        LOGGER.info("Trying to delete '{}' charter '{}' of user '{}'", status, id, author);

        boolean proceed = true;

        if (!isCharterExisting(id, status)) {
            proceed = false;
            LOGGER.info("The '{}' charter '{}' is not existing. Aborting deletion.", status, id);
        }

        if (proceed && status == CharterStatus.PRIVATE && author == null) {
            proceed = false;
            LOGGER.info("Private charters need author information for deletion. Aborting deletion.");
        }

        boolean success = false;

        if (proceed) {

            String uri = Charter.createParentUri(id, status, author);
            String name = Charter.createResourceName(id, status);
            ExistResource resource = new ExistResource(name, uri);

            success = momcaConnection.deleteExistResource(resource);

            if (success) {
                LOGGER.info("Charter '{}' with status '{}' deleted.", id, status);
            } else {
                LOGGER.info("Failed to delete charter '{}' with status '{}'.", id, status);
            }

        }

        return success;

    }

    @Override
    @NotNull
    public Optional<Charter> getCharter(@NotNull IdCharter id, @NotNull CharterStatus status) {

        LOGGER.info("Trying to get charter '{}' with status '{}' from the database.", id, status);

        ExistQuery query = ExistQueryFactory.getResourceUri(id.getContentAsElement(), status.getResourceRoot());
        List<String> results = momcaConnection.queryDatabase(query);

        Optional<Charter> charter;

        if (results.size() > 1) {

            String message = String.format("More than one possible uri for charter '%s' with status '%s' found.",
                    id, status);
            throw new MomcaException(message);

        } else if (results.size() == 1) {

            charter = getCharterFromUri(results.get(0));

        } else {

            charter = Optional.empty();

        }

        LOGGER.info("Returning '{}' for charter '{}' with status '{}' from the database.", charter, id, status);

        return charter;

    }

    @NotNull
    private Optional<Charter> getCharterFromUri(@NotNull String charterUri) {
        return momcaConnection.readExistResource(charterUri).map(Charter::new);
    }

    @Override
    @NotNull
    public List<Charter> getCharterInstances(@NotNull IdCharter id) {

        LOGGER.info("Trying to get all instances for the charter '{}' from the database.", id);

        ExistQuery resourceUri = ExistQueryFactory.getResourceUri(id.getContentAsElement(), null);

        List<Charter> charters = momcaConnection.queryDatabase(resourceUri)
                .stream()
                .map(this::getCharterFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        LOGGER.info("Returning the following instances of the charter '{}' from the database: '{}'", id, charters);

        return charters;

    }

    private boolean isCharterExisting(@NotNull IdCharter idCharter, @Nullable CharterStatus status) {

        ExistQuery query = ExistQueryFactory.checkAtomResourceExistence(idCharter.getContentAsElement(), status.getResourceRoot());
        return !momcaConnection.queryDatabase(query).isEmpty();

    }

    @Override
    public boolean isExisting(@NotNull IdCharter id, @Nullable CharterStatus status) {

        LOGGER.info("Trying to determine the existence of charter '{}' in '{}'.", id, status);

        boolean isCharterExisting = isCharterExisting(id, status == null ? null : status);

        LOGGER.info("Result of query for existence of charter '{}' in '{}': '{}'", id, status, isCharterExisting);

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

            isExisting = fm.isExisting(idFond);

        } else {

            String identifier = hierarchicalUriParts.get(0);
            CollectionManager cm = momcaConnection.getCollectionManager();
            MyCollectionManager mm = momcaConnection.getMyCollectionManager();
            IdCollection idCollection = new IdCollection(identifier);
            IdMyCollection idMyCollection = new IdMyCollection(identifier);

            isExisting = cm.isExisting(idCollection) ||
                    mm.isExisting(idMyCollection, MyCollectionStatus.PRIVATE) ||
                    mm.isExisting(idMyCollection, MyCollectionStatus.PUBLISHED);

        }

        LOGGER.trace("Returning the result of the query for the existence of the parent of charter '{}': '{}'",
                charter.getId(), isExisting);

        return isExisting;
    }

    @Override
    @NotNull
    public List<IdCharter> list(@NotNull IdAbstract parent) {
        return list(parent, CharterStatus.PUBLIC);
    }

    @Override
    @NotNull
    public List<IdCharter> list(@NotNull IdAbstract parent, @NotNull CharterStatus status) {

        LOGGER.info("Trying to list charters belonging to '{}' with status '{}'.", parent, status);

        ExistQuery query = ExistQueryFactory.listCharterAtomIds(parent, status);
        List<String> queryResults = momcaConnection.queryDatabase(query);

        List<IdCharter> ids = createIds(queryResults);

        LOGGER.info("Returning a list of {} charters belonging to '{}' with status '{}'.", ids.size(), parent, status);

        return ids;

    }

    @Override
    @NotNull
    public List<IdCharter> listNotExistingSavedCharters(@NotNull IdUser id) {

        LOGGER.info("Trying to list saved charters that are not existing in the userspace of user '{}'.", id);

        List<IdCharter> results = new ArrayList<>(0);

        momcaConnection.getUserManager().get(id).ifPresent(user ->
                results.addAll(user.getSavedCharters().stream()
                        .map(Saved::getId)
                        .filter(idCharter -> !isCharterExisting(idCharter, CharterStatus.SAVED))
                        .collect(Collectors.toList())));

        LOGGER.info("Returning ids for {} saved charters that are not existing in the userspace of user '{}': {}",
                results.size(), id, results);

        return results;

    }

    @Override
    public boolean publishSavedCharter(@NotNull User user, @NotNull IdCharter id) {

        LOGGER.info("Trying to publish the edited charter '{}' from user '{}'.", id, user);

        List<Saved> savedList = user.getSavedCharters();
        List<Saved> withoutCurrent = user.getSavedCharters()
                .stream()
                .filter(saved -> !id.equals(saved.getId()))
                .collect(Collectors.toList());

        boolean proceed = true;

        if (withoutCurrent.size() == savedList.size()) {
            proceed = false;
            LOGGER.info("Charter to be removed (with the id '{}') is not present in the list of chartes currently" +
                    " edited by user '{}'. Aborting publishing.", id, user);
        }

        if (proceed && !isCharterExisting(id, CharterStatus.SAVED)) {
            proceed = false;
            LOGGER.info("Charter with id '{}' to be published is currently not being edited. Abort publishing.",
                    id);
        }

        boolean success = false;

        if (proceed) {

            ExistQuery query = ExistQueryFactory.publishCharter(id);
            momcaConnection.queryDatabase(query);

            if (isCharterExisting(id, CharterStatus.PUBLIC)) {

                user.setSavedCharters(withoutCurrent);
                success = momcaConnection.getUserManager().updateUserData(user);

                if (success) {
                    LOGGER.info("Charter '{}', edited by user '{}' published.", id, user);
                } else {
                    LOGGER.info("Failed to remove charter with id '{}' from the list of edited charters for user" +
                            " '{}'. Needs to be manually removed.", id, user);
                }

            } else {

                LOGGER.info("Failed to update the status of charter '{}' to 'PUBLISHED'. Aborting publishing.", id);

            }

        }

        return success;

    }

    @Override
    public boolean update(@NotNull Charter updatedCharter) {

        String uri = updatedCharter.getUri();

        LOGGER.info("Trying to update content of charter '{}'.", uri);

        boolean proceed = true;

        if (!isCharterExisting(updatedCharter.getId(), updatedCharter.getCharterStatus())) {
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

    @Override
    public boolean updateId(@NotNull IdCharter newId, @NotNull IdCharter originalId,
                            @NotNull CharterStatus status, @Nullable IdUser author) {

        LOGGER.info("Trying to update id of charter '{}' with status '{}' and creator '{}' to '{}'.",
                originalId, status, author, newId);

        boolean proceed = true;

        if (originalId.equals(newId)) {
            proceed = false;
            LOGGER.info("The original id '{}' is equal to the new id '{}'. Aborting update.", originalId, newId);
        }

        if (proceed && !isCharterExisting(originalId, status)) {
            proceed = false;
            LOGGER.info("There is no charter with id '{}' and status '{}' existing. Aborting update.", originalId, status);
        }

        if (proceed && isCharterExisting(newId, status)) {
            proceed = false;
            LOGGER.info("Charter with id '{}' is already existing for new status '{}'. Aborting update.", newId, status);
        }

        if (proceed && status == CharterStatus.PRIVATE && author == null) {
            proceed = false;
            LOGGER.info("It's not possible to update a private charter without a provided creator. Creator is 'NULL'. Aborting update.");
        }

        if (proceed && !momcaConnection.isCollectionExisting(Charter.createParentUri(newId, status, author))) {
            proceed = false;
            LOGGER.info("Target hierarchy is not existing for id '{}', status '{}' and creator '{}'. Aborting update.", newId, status, author);
        }

        boolean success = false;

        if (proceed) {

            String parentUri = Charter.createParentUri(originalId, status, author);
            String oldAtomId = originalId.getAtomId();
            String newAtomId = newId.getAtomId();
            String newDocumentName = Charter.createResourceName(newId, status);

            ExistQuery query = ExistQueryFactory.updateCharterAtomId(parentUri, oldAtomId, newAtomId, newDocumentName);
            momcaConnection.queryDatabase(query);

            success = isCharterExisting(newId, status);

            if (success) {
                LOGGER.info("Id of '{}' charter updated from '{}' to '{}'.", status, originalId, newId);
            } else {
                LOGGER.info("Failed to update the Id of '{}' charter from '{}' to '{}'.", status, originalId, newId);
            }

        }

        return success;

    }

    @Override
    public boolean updateStatus(@NotNull CharterStatus newStatus, @NotNull CharterStatus originalStatus,
                                @NotNull IdCharter id, @Nullable IdUser author) {

        LOGGER.info("Trying to update status of charter with id '{}' from '{}' to '{}'", id, originalStatus, newStatus);

        boolean proceed = true;

        if (originalStatus.equals(newStatus)) {
            proceed = false;
            LOGGER.info("The original status '{}' is equal to the new status '{}'. Aborting update.", originalStatus, newStatus);
        }

        if (proceed && !isCharterExisting(id, originalStatus)) {
            proceed = false;
            LOGGER.info("There is no charter with id '{}' and status '{}' existing. Aborting update.", id, originalStatus);
        }

        if (proceed && isCharterExisting(id, newStatus)) {
            proceed = false;
            LOGGER.info("Charter with id '{}' is already existing for new status '{}'. Aborting update.", id, newStatus);
        }

        if (proceed && (originalStatus == CharterStatus.PRIVATE || newStatus == CharterStatus.PRIVATE) && author == null) {
            proceed = false;
            LOGGER.info("It's not possible to update to or from status 'PRIVATE' without a provided user. User is 'NULL'. Aborting update.");
        }

        if (proceed && !momcaConnection.isCollectionExisting(Charter.createParentUri(id, newStatus, author))) {
            proceed = false;
            LOGGER.info("Target hierarchy is not existing for id '{}', status '{}' and creator '{}'. Aborting update.", id, newStatus, author);
        }

        boolean success = false;

        if (proceed) {

            String oldParentUri = Charter.createParentUri(id, originalStatus, author);
            String newParentUri = Charter.createParentUri(id, newStatus, author);
            String oldFileName = Charter.createResourceName(id, originalStatus);
            String newFileName = Charter.createResourceName(id, newStatus);

            ExistQuery query = ExistQueryFactory.moveResource(oldParentUri, newParentUri, newFileName, oldFileName);
            momcaConnection.queryDatabase(query);

            success = (isCharterExisting(id, newStatus) && !isCharterExisting(id, originalStatus));

            if (success) {
                LOGGER.info("Status of charter with id '{}' updated from '{}' to '{}'.", id, originalStatus, newStatus);
            } else {
                LOGGER.info("Failed to update status of charter with id '{}' from '{}' to '{}'.", id, originalStatus, newStatus);
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
