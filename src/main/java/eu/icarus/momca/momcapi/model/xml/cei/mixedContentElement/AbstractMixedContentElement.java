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

        this.content = initContent(content, localName);

    }

    @NotNull
    private static Element createXmlContent(@NotNull String content, @NotNull String localName) {

        if (localName.isEmpty()) {
            throw new IllegalArgumentException("LocalName is not allowed to be an empty string");
        }

        if (localName.contains(":")) {
            throw new IllegalArgumentException("Localized content is not supposed to include ':'");
        }

        String stringToParse;

        if (content.startsWith("<" + localName) || content.startsWith("<cei:" + localName)) {

            stringToParse = content;
            stringToParse = stringToParse.replace("<cei:" + localName, "<cei:" + localName + " xmlns:cei='http://www.monasterium.net/NS/cei'");
            stringToParse = stringToParse.replace("</" + localName, "</cei:" + localName);
            stringToParse = stringToParse.replace("<" + localName + " xmlns='http://www.monasterium.net/NS/cei'", "<cei:" + localName + " xmlns:cei='http://www.monasterium.net/NS/cei'");
            stringToParse = stringToParse.replace("<" + localName, "<cei:" + localName + " xmlns:cei='http://www.monasterium.net/NS/cei'");


        } else {

            stringToParse = String.format("<cei:%s xmlns:cei='http://www.monasterium.net/NS/cei'>%s</cei:%s>",
                    localName,
                    content,
                    localName);

        }

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

    protected String initContent(String content, String localName) {

        String result = content;

        if ((content.startsWith("<" + localName) || content.startsWith("<cei:" + localName)) && content.endsWith(localName + ">")) {

            result = result.substring(result.indexOf(">") + 1, result.lastIndexOf("<"));

        }

        return result;

    }

    @Override
    public String toString() {
        return "AbstractMixedContentElement{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }

}
