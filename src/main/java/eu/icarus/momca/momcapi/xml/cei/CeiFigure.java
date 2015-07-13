package eu.icarus.momca.momcapi.xml.cei;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A representation of the {@code cei:figure} element that is used to represents images of the documents.<br/><br/>
 * Example:<br/>
 * {@code <figure n="RS-IAGNS_F1.-fasc.16,-sub.-N-1513_1"><graphic url="RS-IAGNS_F1.-fasc.16,-sub.-N-1513_1.jpg">RS-IAGNS_F1.-fasc.16,-sub.-N-1513_1.jpg</graphic></figure>}
 *
 * @author Daniel Jeller
 *         Created on 09.07.2015.
 */
public class CeiFigure extends Element {

    @NotNull
    private final String n;
    @NotNull
    private final String text;
    @NotNull
    private final String url;


    /**
     * Instantiates a new CeiFigure with a full set of metadata.
     *
     * @param url  The {@code cei:figure/cei:grapic/@url}-attribute responsible for identifying the image in an external storage. Can be either just a relative URI (e.g. {@code image1.jpg}) or an absolute URL ({@code http://server.com/images/image1.jpg}).
     * @param n    The {@code cei:figure/@n}-attribute.
     * @param text The text content of the {@code cei:figure/cei:graphic}-element.
     */
    public CeiFigure(@NotNull String url, @Nullable String n, @Nullable String text) {

        super("cei:figure", Namespace.CEI.getUri());

        if (url.isEmpty()) {
            throw new IllegalArgumentException("An value for 'cei:figure/cei:graphic/@url' is mandatory!");
        }

        this.url = url;
        this.n = (n == null) ? "" : n;
        this.text = (text == null) ? "" : text;

        initXml();

    }

    /**
     * Instantiates a new Cei figure.
     *
     * @param url The {@code cei:figure/cei:grapic/@url}-attribute responsible for identifying the image in an external storage. Can be either just a relative URI (e.g. {@code image1.jpg}) or an absolute URL ({@code http://server.com/images/image1.jpg}).
     */
    public CeiFigure(@NotNull String url) {
        this(url, "", "");
    }

    /**
     * @return The value of the {@code cei:figure/@n}-attribute.
     */
    @NotNull
    public String getN() {
        return n;
    }

    /**
     * @return The value of the {@code cei:figure/cei:graphic}-element.
     */
    @NotNull
    public String getText() {
        return text;
    }

    /**
     * @return The Value of the {@code cei:figure/cei:grapic/@url}-attribute responsible for identifying the image in an external storage. Can be either just a relative URI (e.g. {@code image1.jpg}) or an absolute URL ({@code http://server.com/images/image1.jpg}).
     * @see #hasAbsoluteUrl()
     */
    @NotNull
    public String getUrl() {
        return url;
    }

    /**
     * @return {@code True} if the {@code cei:figure/cei:grapic/@url}-Attribute is an absolute URL (starting with {@code http://} or {@code https://}).
     */
    public boolean hasAbsoluteUrl() {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    @NotNull
    @Override
    public String toString() {

        return "CeiFigure{" +
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
