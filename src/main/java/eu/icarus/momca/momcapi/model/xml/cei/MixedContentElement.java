package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 12/09/2015.
 */
abstract class MixedContentElement extends Element {

    @NotNull
    private String content;

    MixedContentElement(@NotNull String content, @NotNull String localizedRootElementName) {

        super(createXmlContent(content, localizedRootElementName));

        this.content = content;

    }

    @NotNull
    private static Element createXmlContent(@NotNull String content, @NotNull String localizedRootElementName) {

        if (content.isEmpty() || localizedRootElementName.isEmpty()) {
            throw new IllegalArgumentException("Content and localizedRootElementName are not allowed to be an empty string");
        }

        String stringToParse = content;

        if (!content.startsWith("<" + localizedRootElementName) && !content.endsWith(localizedRootElementName + ">")) {
            stringToParse = String.format("<%s xmlns='http://www.monasterium.net/NS/cei'>%s</%s>",
                    localizedRootElementName,
                    content,
                    localizedRootElementName);
        }

        Element xml = Util.parseToElement(removeNamespaceNames(stringToParse));

        if (!xml.getNamespaceURI().equals(Namespace.CEI.getUri())) {
            xml = getXmlWithCorrectedNamespace(xml);
        }

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
