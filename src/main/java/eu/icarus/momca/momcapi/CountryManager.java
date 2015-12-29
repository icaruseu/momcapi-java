package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * The country manager. Performs country- and region-related tasks in the MOM-CA database.
 */
@SuppressWarnings("AccessCanBeTightened")
public interface CountryManager {

    /**
     * Adds a country to the country hierarchy used for archives and fonds.
     *
     * @param country The country to add.
     * @return <code>True</code> if the action was successful.
     */
    boolean addNewCountryToHierarchy(@NotNull Country country);

    /**
     * Adds a region to the country hierarchy used for fonds and archives.
     *
     * @param country The country to add the region to.
     * @param region  The region to add.
     * @return <code>True</code> if the action was successful.
     */
    boolean addRegionToHierarchy(@NotNull Country country, @NotNull Region region);

    /**
     * Deletes a country.
     *
     * @param code The code of the country to delete, e.g. <code>DE</code>.
     * @return <code>True</code> if the action was successful.
     */
    boolean deleteCountryFromHierarchy(@NotNull CountryCode code);

    /**
     * Deletes a region from a countries' hierarchy.
     *
     * @param country    The country to delete from.
     * @param regionName The native name of the region to delete.
     * @return <code>True</code> if the action was successful.
     */
    boolean deleteRegionFromHierarchy(@NotNull Country country, @NotNull String regionName);

    /**
     * Gets a country from the database.
     *
     * @param code The code of the country, e.g. <code>DE</code>.
     * @return The country wrapped in an <code>Optional</code>.
     */
    @NotNull
    Optional<Country> getCountry(@NotNull CountryCode code);

    /**
     * Gets a list of all regions associated with a specific country.
     *
     * @param country The country to associate with.
     * @return A list of all regions.
     */
    @NotNull
    List<Region> getRegions(@NotNull Country country);

    /**
     * Checks if a region is existing in the database.
     *
     * @param country    The country the region is associated with.
     * @param regionName The name of the region to check.
     * @return <code>True</code> if the region exists.
     */
    Boolean isRegionExisting(@NotNull Country country, @NotNull String regionName);

    /**
     * @return A list of the country codes of all countries in the database (archives and collections).
     */
    @NotNull
    List<CountryCode> listCountries();

}
