package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.IdCollection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Collection extends MomcaResource {

    @NotNull
    private final Optional<CountryCode> countryCode;
    @NotNull
    private final IdCollection id;
    @NotNull
    private final String name;
    @NotNull
    private final Optional<String> regionName;

    public Collection(@NotNull MomcaResource momcaResource) {

        super(momcaResource);

        countryCode = initCountryCode();
        regionName = initRegionName();
        id = initId();
        name = initName();

    }

    @NotNull
    public Optional<CountryCode> getCountryCode() {
        return countryCode;
    }

    @NotNull
    public IdCollection getId() {
        return id;
    }

    public String getIdentifier() {
        return id.getCollectionIdentifier();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Optional<String> getRegionName() {
        return regionName;
    }

    private Optional<CountryCode> initCountryCode() {

        Optional<CountryCode> code = Optional.empty();
        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_CEI_COUNTRY_ID);

        if (queryResults.size() == 1) {
            code = Optional.of(new CountryCode(queryResults.get(0)));
        }

        return code;

    }

    private IdCollection initId() {

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_ATOM_ID);

        if (queryResults.isEmpty()) {
            throw new IllegalArgumentException("The content of the resource doesn't contain an atom:id element");
        }

        return new IdCollection(queryResults.get(0));

    }

    private String initName() {

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_CEI_PROVENANCE_TEXT);

        if (queryResults.isEmpty()) {
            throw new IllegalArgumentException("The content of the collection doesn't contain an name.");
        }

        return queryResults.get(0).replaceAll("\\s+", " "); // Normalize whitespace due to nested elements in the xml content

    }

    private Optional<String> initRegionName() {

        Optional<String> name = Optional.empty();

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_CEI_REGION_TEXT);

        if (queryResults.size() == 1) {
            name = Optional.of(queryResults.get(0));
        }

        return name;

    }

}
