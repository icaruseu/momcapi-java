package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.id.IdMyCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.MyCollection;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;
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

            String parentUri = createCollectionUri(myCollection, idUser);
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
    private String createCollectionUri(@NotNull MyCollection myCollection, @NotNull IdUser idUser) {

        String parentUri;

        if (myCollection.getStatus() == MyCollectionStatus.PRIVATE) {

            parentUri = String.format("%s/%s/%s/%s",
                    myCollection.getStatus().getResourceRoot().getUri(),
                    idUser.getIdentifier(),
                    MyCollection.PRIVATE_URI_PART,
                    myCollection.getIdentifier());

        } else {

            parentUri = String.format("%s/%s",
                    MyCollectionStatus.PUBLISHED.getResourceRoot().getUri(),
                    myCollection.getIdentifier());

        }

        return parentUri;

    }

    public void delete(@NotNull IdMyCollection idMyCollection) {

        LOGGER.info("Trying to delete myCollection '{}'.", idMyCollection);

        boolean proceed = true;

        if (!momcaConnection.getCharterManager().listChartersInPrivateMyCollection(idMyCollection).isEmpty()) {
            proceed = false;
            LOGGER.info("There are still existing private charters for collection '{}'.", idMyCollection);
        }

        deletePublic(idMyCollection);

        Optional<MyCollection> privateMyCollection = get(idMyCollection, MyCollectionStatus.PRIVATE);
        privateMyCollection.ifPresent(myCollection -> momcaConnection.deleteCollection(myCollection.getParentUri()));

    }

    public void deletePublic(@NotNull IdMyCollection idMyCollection) {

        CharterManager cm = momcaConnection.getCharterManager();
        if (!cm.listPublicCharters(idMyCollection).isEmpty()) {
            String message = String.format("There are still existing public charters for collection '%s'.",
                    idMyCollection.getIdentifier());
            throw new MomcaException(message);
        }

        String uri = String.format("%s/%s",
                ResourceRoot.PUBLISHED_USER_COLLECTIONS.getUri(), idMyCollection.getIdentifier());
        momcaConnection.deleteCollection(uri);
    }

    @NotNull
    public Optional<MyCollection> get(@NotNull IdMyCollection idCollection, @NotNull MyCollectionStatus status) {

        List<MyCollection> instances = momcaConnection.queryDatabase(
                ExistQueryFactory.getResourceUri(idCollection.getContentAsElement(), status.getResourceRoot()
                )).stream()
                .map(this::getMyCollectionFromUri)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return instances.size() == 0 ? Optional.empty() : Optional.of(instances.get(0));

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
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listMyCollectionsPrivate(idUser));
        return queryResults.stream().map(AtomId::new).map(IdMyCollection::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdMyCollection> listPublicMyCollections() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listMyCollectionsPublic());
        return queryResults.stream().map(AtomId::new).map(IdMyCollection::new).collect(Collectors.toList());
    }

}
