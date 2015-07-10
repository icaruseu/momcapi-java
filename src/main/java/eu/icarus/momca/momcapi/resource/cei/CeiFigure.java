package eu.icarus.momca.momcapi.resource.cei;

import eu.icarus.momca.momcapi.resource.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by daniel on 09.07.2015.
 */
public class CeiFigure extends Element {

    private final boolean hasAbsoluteUrl;
    @NotNull
    private final String n;
    @NotNull
    private final String text;
    @NotNull
    private final String url;

    public CeiFigure(@NotNull String url, @Nullable String n, @Nullable String text) {

        super("cei:figure", Namespace.CEI.getUri());

        if (url.isEmpty()) {
            throw new IllegalArgumentException("An value for 'cei:figure/cei:graphic/@url' is mandatory!");
        }

        this.url = url;
        this.n = (n == null) ? "" : n;
        this.text = (text == null) ? "" : text;

        initXml();

        this.hasAbsoluteUrl = url.startsWith("http://");

    }

    public CeiFigure(@NotNull String url) {
        this(url, "", "");
    }

    @NotNull
    public String getN() {
        return n;
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    public boolean hasAbsoluteUrl() {
        return hasAbsoluteUrl;
    }

    @Override
    public String toString() {

        return "CeiFigure{" +
                "hasAbsoluteUrl=" + hasAbsoluteUrl +
                ", n='" + n + '\'' +
                ", text='" + text + '\'' +
                ", url='" + url + '\'' +
                "} " + super.toString();

    }

    private void initXml() {

        if (!n.isEmpty()) {
            addAttribute(new Attribute("n", n));
        }

        Element ceiGraphic = new Element("cei:graphic", Namespace.CEI.getUri());
        ceiGraphic.addAttribute(new Attribute("url", url));
        if (!text.isEmpty()) {
            ceiGraphic.appendChild(text);
        }
        appendChild(ceiGraphic);

    }

}
