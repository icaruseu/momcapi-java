package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 28/09/2015.
 */
public class Graphic extends Element {

    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private String url = "";

    private Graphic() {
        super("cei:graphic", Namespace.CEI.getUri());
    }

    public Graphic(@NotNull String url) {
        this("", "", "", url);
    }

    public Graphic(@NotNull String facs, @NotNull String id, @NotNull String n, @NotNull String url) {
        this();
        initAttributes(facs, id, n, url);
    }

    public Graphic(@NotNull Element graphicElement) {

        this();

        String facs = graphicElement.getAttributeValue("facs");
        String id = graphicElement.getAttributeValue("id");
        String n = graphicElement.getAttributeValue("n");
        String url = graphicElement.getAttributeValue("url");

        initAttributes(facs, id, n, url);

    }

    @NotNull
    public Optional<String> getFacs() {
        return facs;
    }

    @NotNull
    public Optional<String> getId() {
        return id;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    private void initAttributes(@Nullable String facs, @Nullable String id, @Nullable String n, @NotNull String url) {

        if (facs != null && !facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (id != null && !id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (n != null && !n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

        if (url.isEmpty()) {
            throw new IllegalArgumentException("The url is not allowed to be an empty string.");
        }
        addAttribute(new Attribute("url", url));
        this.url = url;

    }


}
