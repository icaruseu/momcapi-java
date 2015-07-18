package eu.icarus.momca.momcapi.xml.eap;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class EapAbstract extends Element {

    @NotNull
    private final String code;
    @NotNull
    private final String nativeForm;

    public EapAbstract(@NotNull String localRootElementName, @NotNull String code, @NotNull String nativeForm) {

        super(new Element("eap:" + localRootElementName, Namespace.EAP.getUri()));
        initXml(code, nativeForm);

        this.code = code;
        this.nativeForm = nativeForm;

    }

    @NotNull
    public String getCode() {
        return code;
    }

    @NotNull
    public String getNativeForm() {
        return nativeForm;
    }

    private void initXml(@NotNull String code, @NotNull String nativeForm) {

        Element codeElement = new Element("eap:code", Namespace.EAP.getUri());
        codeElement.appendChild(code);
        appendChild(codeElement);

        Element nativeFormElement = new Element("eap:nativeform", Namespace.EAP.getUri());
        nativeFormElement.appendChild(nativeForm);
        appendChild(nativeFormElement);


    }

}
