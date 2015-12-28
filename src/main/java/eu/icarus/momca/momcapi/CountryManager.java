package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 28/12/2015.
 */
public interface CountryManager {

    /**
     * Adds a new country to country hierarchy used for archives and fonds.
     *
     * @param country The country to add.
     * @return true if the process succeeds.
     */
    boolean addNewCountryToHierarchy(@NotNull Country country);

    /**
     * Adds a region to the country hierarchy used for fonds and archives.
     *
     * @param country The country to add the region to.
     * @param region  The region to add to the hierarchy
     * @return true if the addition was successful.
     */

    boolean addRegionToHierarchy(@NotNull Country country, @NotNull Region region);

    /**
     * Deletes a country.
     *
     * @param countryCode The code of the country to delete, e.g. {@code DE}.
     * @return true if the deletion was successful.
     */
    boolean deleteCountryFromHierarchy(@NotNull CountryCode countryCode);

    /**
     * Deletes a region from a countries hierarchy.
     *
     * @param country    The country to delete from.
     * @param regionName The native name of the region to delete.
     * @return true if the deletion was successful.
     */
    boolean deleteRegionFromHierarchy(@NotNull Country country, @NotNull String regionName);

    /**
     * Gets a country from the database.
     *
     * @param countryCode The code of the country, e.g. {@code DE}.
     * @return The country.
     */
    @NotNull
    Optional<Country> getCountry(@NotNull CountryCode countryCode);

    @NotNull
    List<Region> getRegions(@NotNull Country country);

    Boolean isRegionExisting(@NotNull Country country, @NotNull String regionName);

    /**
     * @return A list of the country codes of all countries in the database (archives and collections).
     */
    @NotNull
    List<CountryCode> listCountries();
    
}
