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

    @NotNull
    private static Element getXmlWithCorrectedNamespace(Element xml) {
        xml.setNamespaceURI(Namespace.CEI.getUri());
        xml = Util.parseToElement(xml.toXML().replace("xmlns=\"\"", ""));
        return xml;
    }

    protected String initContent(String content, String localName) {

        String result = content;

        if ((content.startsWith("<" + localName) || content.startsWith("<cei:" + localName)) && content.endsWith(localName + ">")) {

            result = result.substring(result.indexOf(">") + 1, result.lastIndexOf("<"));

        }

        return result;

    }

    @NotNull
    private static String removeNamespaceNames(String stringToParse) {
        return stringToParse.replace("/cei:", "/").replace("<cei:", "<").replace(":cei=", "=");
    }

    @Override
    public String toString() {
        return "AbstractMixedContentElement{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }

}
