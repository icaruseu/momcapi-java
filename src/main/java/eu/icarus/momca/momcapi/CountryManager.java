package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.ResourceRoot;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.xml.eap.EapCountry;
import eu.icarus.momca.momcapi.xml.eap.EapSubdivision;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages countries in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 17.07.2015.
 */
public class CountryManager extends AbstractManager {

    @NotNull
    private static final String MOM_PORTAL_XML_URI = String
            .format("%s/mom.portal.xml", ResourceRoot.PORTAL_HIERARCHY.getUri());

    CountryManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    /**
     * Adds a new country to the database.
     *
     * @param code       The code of the new country to add. Throws an IllegalArgumentException if the code already exists.
     * @param nativeName The name of the country in its own language, e.g. {@code Sverige}.
     * @return The new country.
     */
    @NotNull
    public Country addNewCountryToHierarchy(@NotNull CountryCode code, @NotNull String nativeName) {

        if (isCodeInUseInHierarchy(code.getCode())) {
            throw new IllegalArgumentException(String.format("EapCountry code '%s' is already existing.", code));
        }

        EapCountry newEapCountry = new EapCountry(code.getCode(), nativeName, new ArrayList<>(0));
        ExistQuery query = ExistQueryFactory
                .insertEapElement(MOM_PORTAL_XML_URI, "eap:countries", null, newEapCountry.toXML());
        momcaConnection.queryDatabase(query);

        return getCountry(code).orElseThrow(RuntimeException::new);

    }

    /**
     * Adds a subdivision to the selected eapCountry.
     *
     * @param country    The country to add the region to.
     * @param regionCode The code of the region, e.g. {@code AT-NÖ}. Does not have a specific format.
     * @param nativeName The native name of the subdivision in its native language, e.g. {@code Niederösterreich}.
     * @return The updated eapCountry.
     * @see EapSubdivision
     */
    @NotNull
    public Country addRegionToHierarchy(@NotNull Country country, @NotNull String regionCode, @NotNull String nativeName) {

        Country updated = country;

        List<String> eapCountryXml = momcaConnection.queryDatabase(ExistQueryFactory.getEapCountryXml(country.getCountryCode().getCode()));

        if (!eapCountryXml.isEmpty()) {

            if (eapCountryXml.get(0).contains(regionCode)) {
                throw new IllegalArgumentException(String.format("Re gion '%s' is already existing.", nativeName));
            }

            EapSubdivision eapSubdivision = new EapSubdivision(regionCode, nativeName);
            ExistQuery query = ExistQueryFactory.insertEapElement(
                    MOM_PORTAL_XML_URI, "eap:subdivisions", country.getCountryCode().getCode(), eapSubdivision.toXML());
            momcaConnection.queryDatabase(query);

            updated = getCountry(country.getCountryCode()).orElseThrow(RuntimeException::new);

        }

        return updated;

    }

    /**
     * Deletes a country.
     *
     * @param code The code of the country to delete, e.g. {@code DE}.
     */
    public void deleteCountryFromHierarchy(@NotNull CountryCode code) {

        List<String> archivesForCode = momcaConnection.queryDatabase(ExistQueryFactory.listArchivesForCountry(code));
        if (!archivesForCode.isEmpty()) {
            throw new MomcaException("There are existing archives for country '" + code + "'.");
        }

        momcaConnection.queryDatabase(ExistQueryFactory.deleteEapElement(code.getCode()));

    }

    /**
     * Deletes a region from a countries hierarchy.
     *
     * @param country    The country to delete from.
     * @param regionCode The code of the subdivision to delete.
     * @return The updated country.
     */
    @NotNull
    public Country deleteRegionFromHierarchy(@NotNull Country country, @NotNull String regionCode) {

        List<EapSubdivision> matchingEapSubdivisions = country.getHierarchyXml().getEapSubdivisions().stream()
                .filter(s -> s.getCode().equals(regionCode)).collect(Collectors.toList());

        if (!matchingEapSubdivisions.isEmpty()) {

            String nativeForm = matchingEapSubdivisions.get(0).getNativeform();
            ExistQuery query = ExistQueryFactory.listArchivesForRegion(nativeForm);

            if (!momcaConnection.queryDatabase(query).isEmpty()) {
                throw new MomcaException("There are existing archives for subdivision '" + regionCode + "'.");
            }

        }

        ExistQuery query = ExistQueryFactory.deleteEapElement(regionCode);
        momcaConnection.queryDatabase(query);
        return getCountry(country.getCountryCode()).orElseThrow(RuntimeException::new);

    }

    /**
     * Gets a country from the database.
     *
     * @param code The code of the country, e.g. {@code DE}.
     * @return The country.
     */
    @NotNull
    public Optional<Country> getCountry(@NotNull CountryCode code) {

        Optional<Country> country = Optional.empty();

        if (listCountries().stream().anyMatch(countryCode -> countryCode.equals(code))) {

            List<String> nativeNames = momcaConnection.queryDatabase(ExistQueryFactory.getCountryNativeName(code));

            if (nativeNames.size() > 1) {
                throw new MomcaException("There are multiple names for country code '" + code.getCode() + "'");
            }

            String nativeName = nativeNames.isEmpty() ? "[No name]" : nativeNames.get(0);

            List<String> regionsNames = momcaConnection.queryDatabase(ExistQueryFactory.listRegionsNativeNames(code));
            List<Region> regions = new ArrayList<>(regionsNames.size());

            for (String regionName : regionsNames) {

                List<String> regionCodeResults = momcaConnection.queryDatabase(ExistQueryFactory.getRegionCode(regionName));

                if (regionCodeResults.size() > 1) {
                    throw new MomcaException("More than one region code for region '" + regionName + "' existing.");
                }

                String regionCode = regionCodeResults.isEmpty() ? "" : regionCodeResults.get(0);

                regions.add(new Region(regionCode, regionName));

            }

            country = Optional.of(new Country(code, nativeName, regions));

        }

        return country;

    }

    private boolean isCodeInUseInHierarchy(@NotNull String code) {
        return !momcaConnection.queryDatabase(ExistQueryFactory.getEapCountryXml(code)).isEmpty();
    }

    /**
     * @return A list of the country codes of all countries in the database (archives and collections).
     */
    @NotNull
    public List<CountryCode> listCountries() {
        return momcaConnection.queryDatabase(ExistQueryFactory.listCountryCodes())
                .stream().map(CountryCode::new).collect(Collectors.toList());
    }

}
