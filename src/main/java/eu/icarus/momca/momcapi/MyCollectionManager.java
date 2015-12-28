package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdMyCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.MyCollection;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 29/09/2015.
 */
public class MyCollectionManager extends AbstractManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCollectionManager.class);

    MyCollectionManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    public boolean add(@NotNull MyCollection myCollection) {

        IdMyCollection id = myCollection.getId();
        IdUser idUser = myCollection.getCreator().get();

        LOGGER.info("Trying to add myCollection '{}'", id);

        boolean proceed = true;

        if (isExisting(id, myCollection.getStatus())) {
            proceed = false;
            LOGGER.info("An '{}' myCollection '{}' is already existing. Aborting addition.", myCollection.getStatus(), myCollection.getIdentifier());
        }

        if (proceed && myCollection.getStatus() == MyCollectionStatus.PUBLISHED && !isMyCollectionExisting(id, MyCollectionStatus.PRIVATE)) {
            proceed = false;
            LOGGER.info("Before adding public myCollection '{}', a private version of this myCollection has to exist. Aborting addition.", id);
        }

        if (proceed && !momcaConnection.getUserManager().isExisting(idUser)) {
            proceed = false;
            LOGGER.info("The user '{}' is not existing in the database. Aborting addition.", idUser.getIdentifier());
        }

        boolean success = false;

        if (proceed) {

            String parentUri = createCollectionUri(myCollection.getId(), myCollection.getStatus(), idUser.getIdentifier());
            success = momcaConnection.makeSureCollectionPathExists(parentUri);

            LOGGER.debug("Created parent collection '{}'.", parentUri);

            if (success) {

                String time = momcaConnection.queryRemoteDateTime();
                success = momcaConnection.writeAtomResource(myCollection, time, time);

                if (success) {
                    LOGGER.info("Added myCollection '{}'.", id);
                } else {
                    LOGGER.info("Failed to add myCollection '{}'.", id);
                }

            } else {

                LOGGER.info("Failed to create parent collection '{}'. Aborting addition.", parentUri);

            }

        }

        return success;

    }

    @NotNull
    private String createCollectionUri(@NotNull IdMyCollection idMyCollection, @NotNull MyCollectionStatus status, @NotNull String userIdentifier) {

        String parentUri;

        if (status == MyCollectionStatus.PRIVATE) {

            parentUri = String.format("%s/%s/%s/%s",
                    status.getResourceRoot().getUri(),
                    userIdentifier,
                    MyCollection.PRIVATE_URI_PART,
                    idMyCollection.getIdentifier());

        } else {

            parentUri = String.format("%s/%s",
                    MyCollectionStatus.PUBLISHED.getResourceRoot().getUri(),
                    idMyCollection.getIdentifier());

        }

        return parentUri;

    }

    /**
     * Deletes a public myCollection from the database. Doesn't delete myCollections that still have charters.
     *
     * @param id The myCollection to delete.
     * @return True if the deletion was successful. Note: still returns true, if the method couldn't delete
     * any empty charters' collections.
     */
    public boolean delete(@NotNull IdMyCollection id) {

        return delete(id, null);

    }

    /**
     * Deletes a myCollection from the database. Doesn't delete private myCollections that still have a public
     * myCollection or myCollections that still have charters.
     *
     * @param id     The myCollection to delete.
     * @param idUser The creator of the myColleciton. If @code{NULL}, the myCollection is considered
     *               as a public myCollection.
     * @return True if the deletion was successful. Note: still returns true, if the method couldn't delete
     * any empty charters' collections.
     */
    public boolean delete(@NotNull IdMyCollection id, @Nullable IdUser idUser) {

        MyCollectionStatus status = (idUser == null) ? MyCollectionStatus.PUBLISHED : MyCollectionStatus.PRIVATE;
        CharterManager charterManager = momcaConnection.getCharterManager();

        LOGGER.info("Trying to delete myCollection '{}' with status '{}'.", id, status);

        boolean proceed = true;

        if (!isMyCollectionExisting(id, status)) {
            proceed = false;
            LOGGER.info("MyCollection '{}' with status '{}' is not existing. Aborting deletion.", id, status);
        }

        if (proceed && status == MyCollectionStatus.PRIVATE && isMyCollectionExisting(id, MyCollectionStatus.PUBLISHED)) {
            proceed = false;
            LOGGER.info("There is a public myCollection for private myCollection '{}' existing. Aborting deletion.", id);
        }

        if (proceed && status == MyCollectionStatus.PRIVATE && !charterManager.listChartersInPrivateMyCollection(id).isEmpty()) {
            proceed = false;
            LOGGER.info("There are still existing charters for private myCollection '{}'. Aborting deletion.", id);
        }

        if (proceed && status == MyCollectionStatus.PUBLISHED && !charterManager.listPublicCharters(id).isEmpty()) {
            proceed = false;
            LOGGER.info("There are still existing charters for public myCollection '{}'. Aborting deletion.", id);
        }

        boolean success = false;

        if (proceed) {

            String userIdentifier = idUser == null ? "" : idUser.getIdentifier();
            String myCollectionUri = createCollectionUri(id, status, userIdentifier);
            success = momcaConnection.deleteCollection(myCollectionUri);

            if (success) {

                String chartersCollectionUri = myCollectionUri.replace("metadata.mycollection", "metadata.charter");
                success = momcaConnection.deleteCollection(chartersCollectionUri);

                if (success) {
                    LOGGER.info("MyCollection '{}' deleted.", id);
                } else {
                    success = true;
                    LOGGER.info("Deleted myCollection '{}' but failed to delete empty charters' collection at '{}'.", id, chartersCollectionUri);
                }

            } else {

                LOGGER.info("Failed to delete myCollection '{}'.", id);

            }

        }

        return success;

    }

    @NotNull
    public Optional<MyCollection> get(@NotNull IdMyCollection id, @NotNull MyCollectionStatus status) {

        LOGGER.info("Trying to get myCollection '{}' with status '{}'.", id, status);

        ExistQuery query = ExistQueryFactory.getResourceUri(id.getContentAsElement(), status.getResourceRoot());

        List<MyCollection> instances = momcaConnection
                .queryDatabase(query)
                .stream()
                .map(this::getMyCollectionFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        Optional<MyCollection> result = instances.size() == 0 ? Optional.empty() : Optional.of(instances.get(0));

        LOGGER.info("Returning myCollection '{}': {}", id, result);

        return result;

    }

    @NotNull
    private Optional<MyCollection> getMyCollectionFromUri(@NotNull String myCollectionUri) {

        return momcaConnection.readExistResource(myCollectionUri).map(MyCollection::new);

    }

    public boolean isExisting(@NotNull IdMyCollection idMyCollection, @NotNull MyCollectionStatus myCollectionStatus) {

        LOGGER.info("Trying to determine existence of myCollection '{}' with status '{}'",
                idMyCollection, myCollectionStatus);

        boolean isMyCollectionExisting = isMyCollectionExisting(idMyCollection, myCollectionStatus);

        LOGGER.info("Returning '{}' for the existence of myCollection '{}' with status '{}'",
                isMyCollectionExisting, idMyCollection, myCollectionStatus);

        return isMyCollectionExisting;

    }

    private boolean isMyCollectionExisting(@NotNull IdMyCollection idMyCollection, @NotNull MyCollectionStatus myCollectionStatus) {

        ExistQuery query = ExistQueryFactory.checkMyCollectionExistence(idMyCollection, myCollectionStatus);
        return Util.isTrue(momcaConnection.queryDatabase(query));

    }

    @NotNull
    public List<IdMyCollection> listPrivateMyCollections(@NotNull IdUser idUser) {

        LOGGER.info("Trying to list private myCollections for user '{}'.", idUser);

        ExistQuery query = ExistQueryFactory.listMyCollectionsPrivate(idUser);
        List<IdMyCollection> myCollectionList = queryMyCollections(query);

        LOGGER.info("Returning '{}' myCollections for user '{}'.", myCollectionList.size(), idUser);

        return myCollectionList;

    }

    @NotNull
    public List<IdMyCollection> listPublicMyCollections() {

        LOGGER.info("Trying to list all public myCollections in the database.");

        ExistQuery query = ExistQueryFactory.listMyCollectionsPublic();
        List<IdMyCollection> myCollectionList = queryMyCollections(query);

        LOGGER.info("Returning '{}' public myCollections.", myCollectionList.size());

        return myCollectionList;

    }

    @NotNull
    private List<IdMyCollection> queryMyCollections(@NotNull ExistQuery query) {

        return momcaConnection.queryDatabase(query).stream()
                .map(AtomId::new)
                .map(IdMyCollection::new)
                .collect(Collectors.toList());

    }

}
