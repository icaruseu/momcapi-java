package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.ResourceRoot;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.eap.Country;
import eu.icarus.momca.momcapi.xml.eap.Subdivision;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class HierarchyManager {

    public static final String MOM_PORTAL_XML_URI = String.format("/db/mom-data/%s/mom.portal.xml", ResourceRoot.METADATA_PORTAL_PUBLIC.getCollectionName());
    @NotNull
    private final MomcaConnection momcaConnection;

    HierarchyManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    /**
     * Updates the code of a country.
     *
     * @param country The country.
     * @param newCode The new code.
     * @return The updated country.
     */
    @NotNull
    public Country changeCountryCode(@NotNull Country country, @NotNull String newCode) {
        momcaConnection.queryDatabase(ExistQueryFactory
                .updateElementText(MOM_PORTAL_XML_URI, "eap:code", country.getCode(), newCode));
        return getCountry(newCode).orElseThrow(RuntimeException::new);
    }

    /**
     * Updates the nativeform (== its name in its own language) of a country.
     *
     * @param country       The country.
     * @param newNativeform The new nativeform.
     * @return The updated country.
     */
    @NotNull
    public Country changeCountryNativeform(@NotNull Country country, @NotNull String newNativeform) {
        momcaConnection.queryDatabase(ExistQueryFactory
                .updateElementText(MOM_PORTAL_XML_URI, "eap:nativeform", country.getNativeform(), newNativeform));
        return getCountry(country.getCode()).orElseThrow(RuntimeException::new);
    }

    /**
     * Updates the code of a subdivision of a country.
     *
     * @param country     The country.
     * @param currentCode The current code of the subdivision.
     * @param newCode     The new code.
     * @return The updated country.
     */
    @NotNull
    public Country changeSubdivisionCode(@NotNull Country country, @NotNull String currentCode, @NotNull String newCode) {
        momcaConnection.queryDatabase(ExistQueryFactory
                .updateElementText(MOM_PORTAL_XML_URI, "eap:code", currentCode, newCode));
        return getCountry(country.getCode()).orElseThrow(RuntimeException::new);
    }

    /**
     * Updates the nativeform (== its name in its own language) of a country.
     *
     * @param country           The country.
     * @param currentNativeform the current native form.
     * @param newNativeform     The new nativeform.
     * @return The updated country.
     */
    @NotNull
    public Country changeSubdivisionNativeform(@NotNull Country country, @NotNull String currentNativeform,
                                               @NotNull String newNativeform) {
        momcaConnection.queryDatabase(ExistQueryFactory
                .updateElementText(MOM_PORTAL_XML_URI, "eap:nativeform", currentNativeform, newNativeform));
        return getCountry(country.getCode()).orElseThrow(RuntimeException::new);
    }

    @NotNull
    public Optional<Country> getCountry(@NotNull String code) {

        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.getCountryXml(code));

        if (queryResults.size() > 1) {
            String message = String.format("More than one countries for code '%s' existing. This is not allowed.", code);
            throw new MomcaException(message);
        }


        Optional<Country> country = Optional.empty();

        try {

            Element xml = new Builder().build(queryResults.get(0), null).getRootElement();
            String nativeForm = getNativeform(xml);
            List<Subdivision> subdivisions = getSubdivisions(xml);

            if (!nativeForm.isEmpty()) {
                country = Optional.of(new Country(code, nativeForm, subdivisions));
            }

        } catch (@NotNull ParsingException | IOException e) {
            String message = String.format("Failed to parse xml for country %s", code);
            throw new MomcaException(message);
        }

        return country;

    }

    @NotNull
    public List<String> listCountries() {
        return momcaConnection.queryDatabase(ExistQueryFactory.listCountryCodes());
    }

    @NotNull
    private String getCode(@NotNull Element eapRoot) {
        return eapRoot.getChildElements("code", Namespace.EAP.getUri()).get(0).getValue();
    }

    @NotNull
    private String getNativeform(@NotNull Element eapRoot) {
        return eapRoot.getChildElements("nativeform", Namespace.EAP.getUri()).get(0).getValue();
    }

    @NotNull
    private Elements getSubdivisionElements(@NotNull Element countryElement) {

        Elements subdivisionsElements = countryElement.getChildElements("subdivisions", Namespace.EAP.getUri());

        if (subdivisionsElements.size() != 1) {
            throw new IllegalArgumentException("Element doesn't include a 'eap:subdivisions' element.");
        }

        return subdivisionsElements.get(0).getChildElements("subdivision", Namespace.EAP.getUri());

    }

    @NotNull
    private List<Subdivision> getSubdivisions(@NotNull Element countryElement) {

        Elements subdivisionElements = getSubdivisionElements(countryElement);

        List<Subdivision> subdivisions = new ArrayList<>(0);

        for (int i = 0; i < subdivisionElements.size(); i++) {

            Element subdivisionElement = subdivisionElements.get(i);
            String code = getCode(subdivisionElement);
            String nativeform = getNativeform(subdivisionElement);

            subdivisions.add(new Subdivision(code, nativeform));

        }

        return subdivisions;

    }

}
