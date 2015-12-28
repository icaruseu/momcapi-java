package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.resource.Collection;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.resource.ResourceType;
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
 * Created by djell on 11/08/2015.
 */
public class ExistCollectionExistManager extends AbstractExistManager implements CollectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistCollectionExistManager.class);

    ExistCollectionExistManager(@NotNull ExistMomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @Override
    public boolean add(@NotNull Collection collection) {

        String identifier = collection.getIdentifier();

        LOGGER.info("Try to add collection '{}' to the database.", identifier);

        boolean proceed = true;

        if (isCollectionExisting(collection.getId())) {
            proceed = false;
            LOGGER.info("An collection '{}' is already existing. Aborting addition.", identifier);
        }

        boolean success = false;

        if (proceed) {

            success = momcaConnection.createCollection(identifier, ResourceRoot.ARCHIVAL_COLLECTIONS.getUri());

            if (success) {

                String time = momcaConnection.queryRemoteDateTime();
                success = momcaConnection.writeAtomResource(collection, time, time);

                if (success) {
                    LOGGER.info("Collection '{}' added.", identifier);
                } else {
                    LOGGER.info("Failed to add collection '{}'.", identifier);
                }

            } else {

                LOGGER.info("Failed to create the exist collection for collection '{}'. Aborting addition.", identifier);

            }

        }

        return success;

    }

    private String createCollectionUri(String identifier, String uri) {

        return String.format("%s/%s", uri, identifier);

    }

    private String createResourceUri(@NotNull IdCollection id) {

        return String.format("%s/%s/%s%s",
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                id.getIdentifier(),
                id.getIdentifier(),
                ResourceType.COLLECTION.getNameSuffix());

    }

    @Override
    public boolean delete(@NotNull IdCollection idCollection) {

        String identifier = idCollection.getIdentifier();

        LOGGER.info("Trying to delete the collection '{}'.", identifier);

        boolean proceed = true;

        if (!isCollectionExisting(idCollection)) {
            proceed = false;
            LOGGER.info("Collection '{}' not existing. Aborting delete.", identifier);
        }

        if (proceed &&
                !momcaConnection.getCharterManager().listPublicCharters(idCollection).isEmpty() ||
                !momcaConnection.getCharterManager().listImportedCharters(idCollection).isEmpty()) {
            proceed = false;
            LOGGER.info("There are still existing charters for collection '{}'", identifier);
        }

        boolean success = false;

        if (proceed) {

            String collectionUri = createCollectionUri(identifier, ResourceRoot.ARCHIVAL_COLLECTIONS.getUri());
            success = momcaConnection.deleteCollection(collectionUri);

            if (success) {

                String chartersCollectionUri = createCollectionUri(identifier, ResourceRoot.PUBLIC_CHARTERS.getUri());
                success = momcaConnection.deleteCollection(chartersCollectionUri);

                if (success) {
                    LOGGER.info("Collection '{}' deleted.", identifier);
                } else {
                    success = true;
                    LOGGER.info("Deleted collection '{}' but failed to delete empty charters' collection at '{}'.", identifier, chartersCollectionUri);
                }

            } else {

                LOGGER.info("Failed to delete collection '{}'.", identifier);

            }

        }

        return success;

    }

    @Override
    @NotNull
    public Optional<Collection> get(@NotNull IdCollection idCollection) {

        String identifier = idCollection.getIdentifier();

        LOGGER.info("Trying to get collection '{}' from the database.", identifier);

        String uri = createResourceUri(idCollection);
        Optional<Collection> collection = momcaConnection.readExistResource(uri).map(Collection::new);

        LOGGER.info("Returning '{}' for collection '{}'", collection, identifier);

        return collection;

    }

    private boolean isCollectionExisting(@NotNull IdCollection idCollection) {

        String uri = createResourceUri(idCollection);
        return momcaConnection.isResourceExisting(uri);

    }

    @Override
    public boolean isExisting(@NotNull IdCollection idCollection) {

        LOGGER.info("Try to determine the existance of collection '{}'.", idCollection);

        boolean isCollectionExisting = isCollectionExisting(idCollection);

        LOGGER.info("The result for the query for existence of collection '{}' is '{}'", idCollection, isCollectionExisting);

        return isCollectionExisting;

    }

    @Override
    @NotNull
    public List<IdCollection> list(@NotNull Country country) {

        LOGGER.info("Trying to list all collections for country '{}'.", country);

        ExistQuery query = ExistQueryFactory.listCollectionsForCountry(country.getCountryCode());
        List<IdCollection> collectionList = queryCollections(query);

        LOGGER.info("Returning collections for country '{}': {}", country, collectionList.size());

        return collectionList;

    }

    @Override
    @NotNull
    public List<IdCollection> list(@NotNull Region region) {

        LOGGER.info("Trying to list all collections for region '{}'.", region);

        ExistQuery query = ExistQueryFactory.listCollectionsForRegion(region.getNativeName());
        List<IdCollection> collectionList = queryCollections(query);

        LOGGER.info("Returning collections for region '{}': {}", region, collectionList.size());

        return collectionList;

    }

    @Override
    @NotNull
    public List<IdCollection> list() {

        LOGGER.info("Trying to list all collections in the database.");

        ExistQuery query = ExistQueryFactory.listCollections();
        List<IdCollection> collectionList = queryCollections(query);

        LOGGER.info("Returning collections in the database: {}", collectionList.size());

        return collectionList;

    }

    private List<IdCollection> queryCollections(@NotNull ExistQuery query) {

        return momcaConnection
                .queryDatabase(query)
                .stream()
                .map(AtomId::new)
                .map(IdCollection::new)
                .collect(Collectors.toList());

    }

}
