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
    private final String nativeform;

    public EapAbstract(@NotNull String localRootElementName, @NotNull String code, @NotNull String nativeform) {

        super(new Element("eap:" + localRootElementName, Namespace.EAP.getUri()));
        initXml(code, nativeform);

        this.code = code;
        this.nativeform = nativeform;

    }

    @NotNull
    public String getCode() {
        return code;
    }

    @NotNull
    public String getNativeform() {
        return nativeform;
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
