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

    public void addMyCollection(@NotNull MyCollection myCollection) {

        if (getMyCollection(myCollection.getId(), myCollection.getStatus()).isPresent()) {
            String message = String.format("An '%s' myCollection '%s' is already existing.",
                    myCollection.getStatus(), myCollection.getIdentifier());
            throw new IllegalArgumentException(message);
        }

        if (myCollection.getStatus() == MyCollectionStatus.PUBLISHED &&
                !getMyCollection(myCollection.getId(), MyCollectionStatus.PRIVATE).isPresent()) {
            throw new MomcaException(
                    "Before adding a published MyCollection, a private version of this MyCollection has to exist.");
        }

        IdUser idUser = myCollection.getCreator().get();
        if (!momcaConnection.getUserManager().getUser(idUser).isPresent()) {
            throw new IllegalArgumentException("The user " + idUser.getIdentifier() + " is not existing in the database.");
        }

        String parentCollection;
        if (myCollection.getStatus() == MyCollectionStatus.PRIVATE) {

            String rootCollection = myCollection.getStatus().getResourceRoot().getUri() + "/" + idUser.getIdentifier();
            momcaConnection.createCollection(MyCollection.PRIVATE_URI_PART, rootCollection);

            parentCollection = rootCollection + "/" + MyCollection.PRIVATE_URI_PART;

        } else {

            parentCollection = MyCollectionStatus.PUBLISHED.getResourceRoot().getUri();

        }

        momcaConnection.makeSureCollectionPathExists(parentCollection);
        momcaConnection.createCollection(myCollection.getIdentifier(), parentCollection);

        String time = momcaConnection.queryRemoteDateTime();
        momcaConnection.writeAtomResource(myCollection, time, time);

    }

    public void deleteMyCollection(@NotNull IdMyCollection idMyCollection) {

        CharterManager cm = momcaConnection.getCharterManager();
        if (!cm.listChartersPrivate(idMyCollection).isEmpty()) {
            String message = String.format("There are still existing private charters for collection '%s'.",
                    idMyCollection.getIdentifier());
            throw new MomcaException(message);
        }

        deleteMyCollectionPublic(idMyCollection);

        Optional<MyCollection> privateMyCollection = getMyCollection(idMyCollection, MyCollectionStatus.PRIVATE);
        privateMyCollection.ifPresent(myCollection -> momcaConnection.deleteCollection(myCollection.getParentUri()));

    }

    public void deleteMyCollectionPublic(@NotNull IdMyCollection idMyCollection) {

        CharterManager cm = momcaConnection.getCharterManager();
        if (!cm.listChartersPublic(idMyCollection).isEmpty()) {
            String message = String.format("There are still existing public charters for collection '%s'.",
                    idMyCollection.getIdentifier());
            throw new MomcaException(message);
        }

        String uri = String.format("%s/%s",
                ResourceRoot.PUBLISHED_USER_COLLECTIONS.getUri(), idMyCollection.getIdentifier());
        momcaConnection.deleteCollection(uri);
    }

    @NotNull
    public Optional<MyCollection> getMyCollection(@NotNull IdMyCollection idCollection,
                                                  @NotNull MyCollectionStatus status) {

        List<MyCollection> instances = momcaConnection.queryDatabase(
                ExistQueryFactory.getResourceUri(idCollection.getContentXml(), status.getResourceRoot()
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

    public boolean isMyCollectionExisting(@NotNull IdMyCollection idMyCollection,
                                          @NotNull MyCollectionStatus myCollectionStatus) {

        LOGGER.info("Trying to determine existence of myCollection '{}' with status '{}'",
                idMyCollection, myCollectionStatus);

        ExistQuery query = ExistQueryFactory.checkMyCollectionExistence(idMyCollection, myCollectionStatus);
        List<String> results = momcaConnection.queryDatabase(query);

        if (results.size() != 1) {
            throw new MomcaException("Failed to test for existence of myCollection '" + idMyCollection + "'");
        }

        boolean isExisting = results.get(0).equals("true");

        LOGGER.info("Returning '{}' for the existence of myCollection '{}' with status '{}'",
                isExisting, idMyCollection, myCollectionStatus);

        return isExisting;

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
