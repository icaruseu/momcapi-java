package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.resource.Collection;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 11/08/2015.
 */
public class CollectionManager extends AbstractManager {

    CollectionManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    public void addCollection(@NotNull Collection collection) {

        if (getCollection(collection.getId()).isPresent()) {
            String message = String.format("An collection '%s' is already existing.", collection.getIdentifier());
            throw new IllegalArgumentException(message);
        }

        momcaConnection.createCollection(collection.getIdentifier(), ResourceRoot.ARCHIVAL_COLLECTIONS.getUri());
        String time = momcaConnection.queryRemoteDateTime();
        momcaConnection.writeAtomResource(collection, time, time);

    }

    public void deleteCollection(@NotNull IdCollection idCollection) {

        if (!momcaConnection.getCharterManager().listChartersPublic(idCollection).isEmpty()
                || !momcaConnection.getCharterManager().listChartersImport(idCollection).isEmpty()) {
            String message = String.format("There are still existing charters for collection '%s'",
                    idCollection.getIdentifier());
            throw new IllegalArgumentException(message);
        }

        momcaConnection.deleteCollection(String.format("%s/%s",
                ResourceRoot.PUBLIC_CHARTERS.getUri(), idCollection.getIdentifier()));
        momcaConnection.deleteCollection(String.format("%s/%s",
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(), idCollection.getIdentifier()));

    }

    @NotNull
    public Optional<Collection> getCollection(@NotNull IdCollection idCollection) {
        return getFirstMatchingExistResource(idCollection.getContentXml()).map(Collection::new);
    }

    public boolean isCollectionExisting(@NotNull IdCollection idCollection) {

        String identifier = idCollection.getIdentifier();
        String resourceName = identifier + ResourceType.COLLECTION.getNameSuffix();
        String rootUri = ResourceRoot.ARCHIVAL_COLLECTIONS.getUri();
        String resourceUri = String.join("/", rootUri, identifier, resourceName);

        return momcaConnection.isResourceExisting(resourceUri);

    }

    @NotNull
    public List<IdCollection> listCollections(@NotNull Country country) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listCollectionsForCountry(country.getCountryCode()));
        return queryResults.stream().map(AtomId::new).map(IdCollection::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCollection> listCollections(@NotNull Region region) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listCollectionsForRegion(region.getNativeName()));
        return queryResults.stream().map(AtomId::new).map(IdCollection::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCollection> listCollections() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listCollections());
        return queryResults.stream().map(AtomId::new).map(IdCollection::new).collect(Collectors.toList());
    }

}
