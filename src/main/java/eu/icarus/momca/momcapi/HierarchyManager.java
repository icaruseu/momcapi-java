package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
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

    @NotNull
    private final MomcaConnection momcaConnection;

    HierarchyManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
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

        } catch (ParsingException | IOException e) {
            String message = String.format("Failed to parse xml for country %s", code);
            throw new MomcaException(message);
        }

        return country;

    }

    @NotNull
    public List<String> listCountries() {
        return momcaConnection.queryDatabase(ExistQueryFactory.listCountries());
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
