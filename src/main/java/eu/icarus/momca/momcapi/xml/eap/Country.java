package eu.icarus.momca.momcapi.xml.eap;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Country extends EapAbstract {

    @NotNull
    private final List<Subdivision> subdivisions;

    public Country(@NotNull String code, @NotNull String nativeForm, @NotNull List<Subdivision> subdivisions) {

        super("country", code, nativeForm);

        Element subdivisionsElement = new Element("eap:subdivisions", Namespace.EAP.getUri());
        subdivisions.forEach(subdivisionsElement::appendChild);
        appendChild(subdivisionsElement);

        this.subdivisions = subdivisions;

    }

    @NotNull
    public List<Subdivision> getSubdivisions() {
        return subdivisions;
    }

}
