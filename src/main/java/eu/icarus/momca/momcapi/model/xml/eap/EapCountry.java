package eu.icarus.momca.momcapi.model.xml.eap;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class EapCountry extends EapAbstract {

    @NotNull
    private final List<EapSubdivision> eapSubdivisions;

    public EapCountry(@NotNull String code, @NotNull String nativeForm, @NotNull List<EapSubdivision> eapSubdivisions) {

        super("country", code, nativeForm);

        Element subdivisionsElement = new Element("eap:subdivisions", Namespace.EAP.getUri());
        eapSubdivisions.forEach(subdivisionsElement::appendChild);
        appendChild(subdivisionsElement);

        this.eapSubdivisions = eapSubdivisions;

    }

    @NotNull
    public List<EapSubdivision> getEapSubdivisions() {
        return eapSubdivisions;
    }

}
