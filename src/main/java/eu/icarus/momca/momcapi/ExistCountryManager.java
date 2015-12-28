package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.xml.eap.EapCountry;
import eu.icarus.momca.momcapi.model.xml.eap.EapSubdivision;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ExistCountryManager extends AbstractManager implements CountryManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistCountryManager.class);

    @NotNull
    private static final String MOM_PORTAL_XML_URI = String
            .format("%s/mom.portal.xml", ResourceRoot.PORTAL_HIERARCHY.getUri());

    ExistCountryManager(@NotNull ExistMomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @Override
    public boolean addNewCountryToHierarchy(@NotNull Country country) {

        String code = country.getCountryCode().getCode();
        String name = country.getNativeName();

        LOGGER.info("Trying to add country '{}' to the hierarchy.", code);

        boolean proceed = true;

        if (isCodeInUseInHierarchy(code)) {
            proceed = false;
            LOGGER.info("Country code '{}' is already existing. Aborting addition.", code);
        }

        boolean success = false;

        if (proceed) {

            EapCountry newEapCountry = new EapCountry(code, name, new ArrayList<>(0));

            ExistQuery query = ExistQueryFactory.insertEapElement(MOM_PORTAL_XML_URI, "eap:countries", null, newEapCountry.toXML());
            success = Util.isTrue(momcaConnection.queryDatabase(query));

            if (success) {
                LOGGER.info("Country '{}' added to the hierarchy.", name);
            } else {
                LOGGER.info("Failed to add country '{}' to the hierarchy.", name);
            }

        }

        return success;

    }

    @Override
    public boolean addRegionToHierarchy(@NotNull Country country, @NotNull Region region) {

        String regionNativeName = region.getNativeName();
        CountryCode countryCode = country.getCountryCode();

        LOGGER.info("Trying to add region '{}' to country '{}'.", regionNativeName, countryCode);

        boolean proceed = true;

        if (getRegions(country).contains(region)) {
            proceed = false;
            LOGGER.info("Region '{}' is already existing. Aborting addition.", regionNativeName);
        }

        if (proceed && !region.getCode().isPresent()) {
            proceed = false;
            LOGGER.info("A hierarchical region needs to contain a region code. Aborting addition.");
        }

        boolean success = false;

        if (proceed) {

            EapSubdivision eapSubdivision = new EapSubdivision(region.getCode().get(), regionNativeName);

            ExistQuery query = ExistQueryFactory.insertEapElement(MOM_PORTAL_XML_URI, "eap:subdivisions",
                    countryCode.getCode(), eapSubdivision.toXML());
            success = Util.isTrue(momcaConnection.queryDatabase(query));

            if (success) {
                LOGGER.info("Region '{}' added to country '{}'.", regionNativeName, countryCode);
            } else {
                LOGGER.info("Failed to add region '{}' to country '{}'.", regionNativeName, countryCode);
            }

        }

        return success;

    }

    @Override
    public boolean deleteCountryFromHierarchy(@NotNull CountryCode countryCode) {

        String code = countryCode.getCode();

        LOGGER.info("Trying to delete the country code '{}' from the hierarchy.", code);

        boolean proceed = true;

        if (!momcaConnection.queryDatabase(ExistQueryFactory.listArchivesForCountry(countryCode)).isEmpty()) {
            proceed = false;
            LOGGER.info("There are existing archives for country '{}'.", code);
        }

        boolean success = false;

        if (proceed) {

            ExistQuery query = ExistQueryFactory.deleteEapElement(code);
            success = Util.isTrue(momcaConnection.queryDatabase(query));

            if (success) {
                LOGGER.info("Deleted country conde '{}' from the hierarchy.", code);
            } else {
                LOGGER.info("Failed to delete country conde '{}' from the hierarchy.", code);
            }

        }

        return success;

    }

    @Override
    public boolean deleteRegionFromHierarchy(@NotNull Country country, @NotNull String regionName) {

        String nativeName = country.getNativeName();

        LOGGER.info("Trying to delete the region '{}' from the country '{}'.", regionName, nativeName);

        boolean proceed = true;

        if (!getRegions(country).stream().anyMatch(region -> regionName.equals(region.getNativeName()))) {
            proceed = false;
            LOGGER.info("The region '{}' does not exist in country '{}'. Aborting delete.", regionName, nativeName);
        }

        if (proceed && !momcaConnection.queryDatabase(ExistQueryFactory.listArchivesForRegion(regionName)).isEmpty()) {
            proceed = false;
            LOGGER.info("There are existing archives for region '{}'.", regionName);
        }

        boolean success = false;

        if (proceed) {

            ExistQuery query = ExistQueryFactory.deleteEapElement(regionName);
            success = Util.isTrue(momcaConnection.queryDatabase(query));

            if (success) {
                LOGGER.info("Region '{}' deleted from country '{}'.", regionName, nativeName);
            } else {
                LOGGER.info("Failed to delete region '{}' from country '{}'.", regionName, nativeName);
            }

        }

        return success;

    }

    @Override
    @NotNull
    public Optional<Country> getCountry(@NotNull CountryCode countryCode) {

        String code = countryCode.getCode();

        LOGGER.info("Trying to get the country for code '{}' from the database.", code);

        Optional<Country> country = Optional.empty();

        if (listCountries().stream().anyMatch(c -> c.equals(countryCode))) {

            List<String> nativeNames = momcaConnection.queryDatabase(ExistQueryFactory.getCountryNativeName(countryCode));

            if (nativeNames.size() > 1) {
                throw new MomcaException("There are multiple names for country code '" + code + "'");
            }

            String nativeName = nativeNames.isEmpty() ? "[No name]" : nativeNames.get(0);
            country = Optional.of(new Country(countryCode, nativeName));

        }

        LOGGER.info("Returning '{}' as the country for code '{}'.", country, code);

        return country;

    }

    @Override
    @NotNull
    public List<Region> getRegions(@NotNull Country country) {

        String countryCode = country.getCountryCode().getCode();

        LOGGER.info("Try to get the regions for country '{}' from the database.", countryCode);

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

        LOGGER.info("Returning the following number of regions for country '{}': {}", countryCode, regions.size());

        return regions;

    }

    private boolean isCodeInUseInHierarchy(@NotNull String code) {
        return !momcaConnection.queryDatabase(ExistQueryFactory.getEapCountryXml(code)).isEmpty();
    }

    @Override
    public Boolean isRegionExisting(@NotNull Country country, @NotNull String regionName) {

        CountryCode countryCode = country.getCountryCode();

        LOGGER.info("Try to determine if the region '{}' is existing for country '{}'.", regionName, countryCode);

        boolean isRegionExisting = momcaConnection
                .getCountryManager()
                .getRegions(country)
                .stream()
                .anyMatch(region -> regionName.equals(region.getNativeName()));

        LOGGER.info("Result for query for existence of region '{}' in country '{}': {}", regionName, countryCode, isRegionExisting);

        return isRegionExisting;

    }

    @Override
    @NotNull
    public List<CountryCode> listCountries() {

        LOGGER.info("Trying to list all countries in the database.");

        List<CountryCode> codeList = momcaConnection.queryDatabase(ExistQueryFactory.listCountryCodes())
                .stream().map(CountryCode::new).collect(Collectors.toList());

        LOGGER.info("Returning list of {} countries.", codeList.size());

        return codeList;
    }

}
