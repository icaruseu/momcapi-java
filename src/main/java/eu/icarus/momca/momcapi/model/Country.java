package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.xml.eap.EapCountry;
import eu.icarus.momca.momcapi.xml.eap.EapSubdivision;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by djell on 11/08/2015.
 */
public class Country {

    @NotNull
    private final CountryCode countryCode;
    @NotNull
    private final String nativeName;
    @NotNull
    private final List<Region> regions;

    public Country(@NotNull CountryCode countryCode, @NotNull String nativeName, @NotNull List<Region> regions) {
        this.countryCode = countryCode;
        this.nativeName = nativeName;
        this.regions = regions;
    }

    public Country(@NotNull CountryCode countryCode, @NotNull String nativeName) {
        this.countryCode = countryCode;
        this.nativeName = nativeName;
        this.regions = new ArrayList<Region>(0);
    }

    @NotNull
    public CountryCode getCountryCode() {
        return countryCode;
    }

    public EapCountry getHierarchyXml() {
        String code = countryCode.getCode();
        List<EapSubdivision> eapSubdivisions = regions.stream()
                .map(region
                        -> new EapSubdivision(region.getCode()
                        .orElse(code + "-" + region.getNativeName()), region.getNativeName()))
                .collect(Collectors.toList());
        return new EapCountry(countryCode.getCode(), nativeName, eapSubdivisions);
    }

    @NotNull
    public String getNativeName() {
        return nativeName;
    }

    @NotNull
    public List<Region> getRegions() {
        return regions;
    }

}
