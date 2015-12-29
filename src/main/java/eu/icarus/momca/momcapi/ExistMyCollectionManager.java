package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.CharterStatus;
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
 * An implementation of <code>MyCollectionManager</code> based on an eXist MOM-CA connection.
 */
class ExistMyCollectionManager extends AbstractExistManager implements MyCollectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistMyCollectionManager.class);

    /**
     * Creates a myCollection manager instance.
     *
     * @param momcaConnection The MOM-CA connection.
     */
    ExistMyCollectionManager(@NotNull ExistMomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @Override
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
            success = momcaConnection.createCollectionPath(parentUri);

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

    @SuppressWarnings("PublicMethodWithoutLogging")
    @Override
    public boolean delete(@NotNull IdMyCollection id) {
        return delete(id, null);
    }

    @Override
    public boolean delete(@NotNull IdMyCollection id, @Nullable IdUser user) {

        MyCollectionStatus status = (user == null) ? MyCollectionStatus.PUBLISHED : MyCollectionStatus.PRIVATE;
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

        if (proceed && status == MyCollectionStatus.PRIVATE && !charterManager.list(id, CharterStatus.PRIVATE).isEmpty()) {
            proceed = false;
            LOGGER.info("There are still existing charters for private myCollection '{}'. Aborting deletion.", id);
        }

        if (proceed && status == MyCollectionStatus.PUBLISHED && !charterManager.list(id).isEmpty()) {
            proceed = false;
            LOGGER.info("There are still existing charters for public myCollection '{}'. Aborting deletion.", id);
        }

        boolean success = false;

        if (proceed) {

            String userIdentifier = user == null ? "" : user.getIdentifier();
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

    @Override
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

    @Override
    public boolean isExisting(@NotNull IdMyCollection id, @NotNull MyCollectionStatus status) {

        LOGGER.info("Trying to determine existence of myCollection '{}' with status '{}'",
                id, status);

        boolean isMyCollectionExisting = isMyCollectionExisting(id, status);

        LOGGER.info("Returning '{}' for the existence of myCollection '{}' with status '{}'",
                isMyCollectionExisting, id, status);

        return isMyCollectionExisting;

    }

    private boolean isMyCollectionExisting(@NotNull IdMyCollection idMyCollection, @NotNull MyCollectionStatus myCollectionStatus) {

        ExistQuery query = ExistQueryFactory.checkMyCollectionExistence(idMyCollection, myCollectionStatus);
        return Util.isTrue(momcaConnection.queryDatabase(query));

    }

    @NotNull
    @Override
    public List<IdMyCollection> list(@NotNull MyCollectionStatus status, @Nullable IdUser owner) {

        LOGGER.info("Trying to list all myCollections that match status '{}' and owner '{}'.", status, owner);

        ExistQuery query = ExistQueryFactory.listMyCollections(status, owner);
        List<IdMyCollection> myCollections = queryMyCollections(query);

        LOGGER.info("Returning list of {} myCollections that match status '{}' and owner '{}'.", myCollections.size(), status, owner);

        return myCollections;

    }

    @NotNull
    private List<IdMyCollection> queryMyCollections(@NotNull ExistQuery query) {

        return momcaConnection.queryDatabase(query).stream()
                .map(AtomId::new)
                .map(IdMyCollection::new)
                .collect(Collectors.toList());

    }

}
