package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Country;
import eu.icarus.momca.momcapi.resource.Region;
import eu.icarus.momca.momcapi.xml.atom.IdCollection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by djell on 11/08/2015.
 */
public class CollectionManager extends AbstractManager {

    public CollectionManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @NotNull
    public List<IdCollection> listCollections() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listCollections());
        return queryResults.stream().map(IdCollection::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCollection> listCollections(@NotNull Country country) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listCollectionsForCountry(country.getCountryCode()));
        return queryResults.stream().map(IdCollection::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCollection> listCollections(@NotNull Region region) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listCollectionsForRegion(region.getNativeName()));
        return queryResults.stream().map(IdCollection::new).collect(Collectors.toList());
    }

}
