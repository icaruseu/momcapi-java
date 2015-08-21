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
     * Adds a new country to country hierarchy used for archives and fonds.
     *
     * @param country The country to add.
     */
    @NotNull
    public void addNewCountryToHierarchy(@NotNull Country country) {

        String code = country.getCountryCode().getCode();
        String name = country.getNativeName();

        if (isCodeInUseInHierarchy(code)) {
            throw new IllegalArgumentException(String.format("EapCountry code '%s' is already existing.", code));
        }

        EapCountry newEapCountry = new EapCountry(code, name, new ArrayList<>(0));
        ExistQuery query = ExistQueryFactory
                .insertEapElement(MOM_PORTAL_XML_URI, "eap:countries", null, newEapCountry.toXML());
        momcaConnection.queryDatabase(query);

    }

    /**
     * Adds a region to the country hierarchy used for fonds and archives.
     *
     * @param country The country to add the region to.
     * @param region  The region to add to the hierarchy
     */

    public void addRegionToHierarchy(@NotNull Country country, @NotNull Region region) {

        if (getRegions(country).contains(region)) {
            throw new IllegalArgumentException(String.format("Region '%s' is already existing.", region.getNativeName()));
        }

        if (!region.getCode().isPresent()) {
            throw new IllegalArgumentException("A hierarchical region needs to contain a region code.");
        }

        EapSubdivision eapSubdivision = new EapSubdivision(region.getCode().get(), region.getNativeName());
        ExistQuery query = ExistQueryFactory.insertEapElement(
                MOM_PORTAL_XML_URI, "eap:subdivisions", country.getCountryCode().getCode(), eapSubdivision.toXML());
        momcaConnection.queryDatabase(query);

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
     * @param regionName The native name of the region to delete
     */
    @NotNull
    public void deleteRegionFromHierarchy(@NotNull Country country, @NotNull String regionName) {

        if (getRegions(country).stream().anyMatch(region -> regionName.equals(region.getNativeName()))) {

            if (!momcaConnection.queryDatabase(
                    ExistQueryFactory.listArchivesForRegion(regionName)).isEmpty()) {
                String message = String.format("There are existing archives for region '%s'.", regionName);
                throw new MomcaException(message);
            }

            momcaConnection.queryDatabase(ExistQueryFactory.deleteEapElement(regionName));

        }

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
            country = Optional.of(new Country(code, nativeName));

        }

        return country;

    }

    @NotNull
    public List<Region> getRegions(@NotNull Country country) {

        List<Region> regions = new ArrayList<>(0);

        List<String> regionNames =
                momcaConnection.queryDatabase(ExistQueryFactory.listRegionsNativeNames(country.getCountryCode()));

        for (String name : regionNames) {
            List<String> codeList = momcaConnection.queryDatabase(ExistQueryFactory.getRegionCode(name));

            if (codeList.size() > 1) {
                throw new MomcaException(String.format("There are multiple codes for region '%s': %s", name, codeList.toString()));
            }

            String code = codeList.isEmpty() ? "" : codeList.get(0);
            regions.add(new Region(code, name));

        }

        return regions;

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
