package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 12/09/2015.
 */
public abstract class AbstractMixedContentElement extends Element {

    @NotNull
    private String content = "";

    AbstractMixedContentElement(@NotNull String localName) {
        super("cei:" + localName, Namespace.CEI.getUri());
    }

    AbstractMixedContentElement(@NotNull String content, @NotNull String localName) {

        super(createXmlContent(content, localName));

        this.content = Util.joinChildNodes(this);

    }

    @NotNull
    private static Element createXmlContent(@NotNull String content, @NotNull String localName) {

        if (localName.isEmpty()) {
            throw new IllegalArgumentException("LocalName is not allowed to be an empty string");
        }

        if (localName.contains(":")) {
            throw new IllegalArgumentException("Localized content is not supposed to include ':'");
        }

        Element xml;

        if ((content.startsWith("<" + localName) || content.startsWith("<cei:" + localName)) && content.endsWith(localName + ">")) {
            xml = Util.parseToElement(content);
        } else {
            String temp = String.format("<cei:%s xmlns:cei='http://www.monasterium.net/NS/cei' >%s</cei:%s>", localName, content, localName);
            xml = Util.parseToElement(temp);
        }

        Util.changeNamespace(xml, Namespace.CEI);

        return xml;

    }

    @NotNull
    public String getContent() {
        return content;
    }

    static String initContent(@NotNull Element element, @NotNull String localName) {

        if (!element.getLocalName().equals(localName)) {
            String message = String.format(
                    "The provided element doesn't have the correct root element, 'cei:%s' but '%s'.",
                    localName, element.getQualifiedName());
            throw new IllegalArgumentException(message);
        }

        return Util.joinChildNodes(element);

    }

    @Override
    public String toString() {
        return "AbstractMixedContentElement{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }

}
