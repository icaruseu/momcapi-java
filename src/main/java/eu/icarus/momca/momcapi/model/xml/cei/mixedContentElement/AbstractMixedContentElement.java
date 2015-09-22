package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 12/09/2015.
 */
abstract class AbstractMixedContentElement extends Element {

    @NotNull
    private String content = "";

    AbstractMixedContentElement(@NotNull String localName) {
        super("cei:" + localName, Namespace.CEI.getUri());
    }

    AbstractMixedContentElement(@NotNull String content, @NotNull String localName) {

        super(createXmlContent(content, localName));

        this.content = content;

    }

    @NotNull
    private static Element createXmlContent(@NotNull String content, @NotNull String localizedRootElementName) {

        if (localizedRootElementName.isEmpty()) {
            throw new IllegalArgumentException("LocalizedRootElementName are not allowed to be an empty string");
        }

        if (localizedRootElementName.contains(":")) {
            throw new IllegalArgumentException("Localized content is not supposed to include ':'");
        }


        String stringToParse = String.format("<cei:%s xmlns:cei='http://www.monasterium.net/NS/cei'>%s</cei:%s>",
                localizedRootElementName,
                content,
                localizedRootElementName);

        Element xml = Util.parseToElement(stringToParse);

        return xml;

    }

    @NotNull
    private static Element getXmlWithCorrectedNamespace(Element xml) {
        xml.setNamespaceURI(Namespace.CEI.getUri());
        xml = Util.parseToElement(xml.toXML().replace("xmlns=\"\"", ""));
        return xml;
    }

    @NotNull
    private static String removeNamespaceNames(String stringToParse) {
        return stringToParse.replace("/cei:", "/").replace("<cei:", "<").replace(":cei=", "=");
    }

    @NotNull
    public String getContent() {
        return content;
    }

}
