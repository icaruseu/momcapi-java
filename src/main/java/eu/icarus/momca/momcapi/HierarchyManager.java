package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.xml.eap.Country;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class HierarchyManager {

    @NotNull
    private final MomcaConnection momcaConnection;

    HierarchyManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    @NotNull
    public Optional<Country> getCountry(@NotNull String code) {

        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.getCountryXml(code));
        System.out.println(queryResults);

        return Optional.empty();

    }

    @NotNull
    public List<String> listCountries() {
        return momcaConnection.queryDatabase(ExistQueryFactory.listCountries());
    }


}
