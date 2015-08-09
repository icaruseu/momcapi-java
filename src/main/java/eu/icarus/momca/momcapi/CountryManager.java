package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.ResourceRoot;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.eap.Country;
import eu.icarus.momca.momcapi.xml.eap.EapAbstract;
import eu.icarus.momca.momcapi.xml.eap.Subdivision;
import nu.xom.Element;
import nu.xom.Elements;
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
            .format("/db/mom-data/%s/mom.portal.xml", ResourceRoot.METADATA_PORTAL_PUBLIC.getCollectionName());

    CountryManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    /**
     * Adds a new country to the database.
     *
     * @param code       The code of the new country to add. Throws an IllegalArgumentException if the code already exists.
     * @param nativeform The name of the country in its own language, e.g. {@code Sverige}.
     * @return The new country.
     */
    @NotNull
    public Country addCountry(@NotNull String code, @NotNull String nativeform) {

        if (isCodeAlreadyExisting(code)) {
            throw new IllegalArgumentException(String.format("Country code '%s' is already existing.", code));
        }

        Country newCountry = new Country(code, nativeform, new ArrayList<>(0));
        ExistQuery query = ExistQueryFactory
                .insertEapElement(MOM_PORTAL_XML_URI, "eap:countries", null, newCountry.toXML());
        momcaConnection.queryDatabase(query);

        return getCountry(code).orElseThrow(RuntimeException::new);

    }

    /**
     * Adds a subdivision to the selected country.
     *
     * @param country    The country to add the subdivision to.
     * @param code       The code of the subdivision, e.g. {@code AT-NÖ}.
     * @param nativeform The nativeform of the subdivision in its native language, e.g. {@code Niederösterreich}.
     * @return The updated country.
     * @see Subdivision
     */
    @NotNull
    public Country addSubdivision(@NotNull Country country, @NotNull String code, @NotNull String nativeform) {

        if (isCodeAlreadyExisting(code)) {
            throw new IllegalArgumentException(String.format("Subdivision code '%s' is already existing.", code));
        }

        Subdivision subdivision = new Subdivision(code, nativeform);
        ExistQuery query = ExistQueryFactory.insertEapElement(
                MOM_PORTAL_XML_URI, "eap:subdivisions", country.getCode(), subdivision.toXML());
        momcaConnection.queryDatabase(query);

        return getCountry(country.getCode()).orElseThrow(RuntimeException::new);

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
     * @see Subdivision
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
     * @see Subdivision
     */
    @NotNull
    public Country changeSubdivisionNativeform(@NotNull Country country, @NotNull String currentNativeform,
                                               @NotNull String newNativeform) {
        momcaConnection.queryDatabase(ExistQueryFactory
                .updateElementText(MOM_PORTAL_XML_URI, "eap:nativeform", currentNativeform, newNativeform));
        return getCountry(country.getCode()).orElseThrow(RuntimeException::new);
    }

    /**
     * Deletes a country. If there are still archives using this country in the database, a
     * {@code MomcaException} is thrown.
     *
     * @param code The code of the country to delete, e.g. {@code DE}.
     */
    public void deleteCountry(@NotNull String code) {

        List<String> archivesForCode = momcaConnection.queryDatabase(ExistQueryFactory.listIdArchivesForCountry(code));
        if (!archivesForCode.isEmpty()) {
            throw new MomcaException("There are existing archives for country '" + code + "'.");
        }

        momcaConnection.queryDatabase(ExistQueryFactory.deleteEapElement(code));

    }

    /**
     * Deletes a subdivison from a country. If there are still archives using this subdivision in the database, a
     * {@code MomcaException} is thrown.
     *
     * @param country The country to delete from.
     * @param code    The code of the subdivision to delete.
     * @return The updated country.
     * @see Subdivision
     */
    @NotNull
    public Country deleteSubdivision(@NotNull Country country, @NotNull String code) {

        List<Subdivision> matchingSubdivisions = country.getSubdivisions().stream()
                .filter(s -> s.getCode().equals(code)).collect(Collectors.toList());

        if (!matchingSubdivisions.isEmpty()) {

            String nativeForm = matchingSubdivisions.get(0).getNativeform();
            ExistQuery query = ExistQueryFactory.listIdArchivesForSubdivision(nativeForm);

            if (!momcaConnection.queryDatabase(query).isEmpty()) {
                throw new MomcaException("There are existing archives for subdivision '" + code + "'.");
            }

        }

        ExistQuery query = ExistQueryFactory.deleteEapElement(code);
        momcaConnection.queryDatabase(query);
        return getCountry(country.getCode()).orElseThrow(RuntimeException::new);

    }

    /**
     * Gets a country from the database.
     *
     * @param code The code of the country, e.g. {@code DE}.
     * @return The country.
     */
    @NotNull
    public Optional<Country> getCountry(@NotNull String code) {

        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.getCountryXml(code));

        if (queryResults.isEmpty()) {
            return Optional.empty();
        }

        if (queryResults.size() > 1) {
            String message = String.format("More than one countries for code '%s' existing. This is not allowed.", code);
            throw new MomcaException(message);
        }

        Element xml = Util.parseXml(queryResults.get(0));
        String nativeForm = getNativeform(xml);
        List<Subdivision> subdivisions = getSubdivisions(xml);

        return Optional.of(new Country(code, nativeForm, subdivisions));

    }

    /**
     * @return A list of the codes (e.g. {@code ["DE", "SE", "AT"]) of all countries in the database.
     */
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

    private boolean isCodeAlreadyExisting(String code) {

        List<String> allCountryCodes = momcaConnection.queryDatabase(ExistQueryFactory.listCountryCodes());

        if (allCountryCodes.stream().anyMatch(countryCode -> countryCode.equals(code))) {

            return true;

        } else {

            for (String countryCode : allCountryCodes) {
                Country country = getCountry(countryCode).get();
                if (country.getSubdivisions().stream().map(EapAbstract::getCode).anyMatch(s -> s.equals(code))) {
                    return true;
                }
            }

        }

        return false;

    }

}
