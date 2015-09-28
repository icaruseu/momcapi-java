package eu.icarus.momca.momcapi.model.xml.xrx;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 28/09/2015.
 */
public class Keywords extends Element {

    public static final String XRX_URI = Namespace.XRX.getUri();
    @NotNull
    private String keyword;

    public Keywords(@NotNull String keyword) {

        super("xrx:keywords", XRX_URI);

        this.keyword = keyword;

        Element keywordElement = new Element("xrx:keyword", XRX_URI);
        keywordElement.appendChild(keyword);
        appendChild(keywordElement);

    }

    @NotNull
    public String getKeyword() {
        return keyword;
    }

}
