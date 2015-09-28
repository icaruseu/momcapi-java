package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.cei.Ref;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 25/09/2015.
 */
public class Nota extends AbstractMixedContentElement {

    public static final String CEI_URI = Namespace.CEI.getUri();
    public static final String LOCAL_NAME = "nota";

    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> lang = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private Optional<String> position = Optional.empty();
    @NotNull
    private Optional<String> type = Optional.empty();

    public Nota(@NotNull String content) {
        super(content, LOCAL_NAME);
    }

    public Nota(@NotNull String content,
                @NotNull String facs, @NotNull String id, @NotNull String lang, @NotNull String n,
                @NotNull String position, @NotNull String type) {
        this(content);
        initAttributes(facs, id, lang, n, position, type);
    }

    public Nota(@NotNull Element archIdentifierElement) {

        this(initContent(archIdentifierElement, LOCAL_NAME));

        String facs = archIdentifierElement.getAttributeValue("facs");
        String id = archIdentifierElement.getAttributeValue("id");
        String lang = archIdentifierElement.getAttributeValue("lang");
        String n = archIdentifierElement.getAttributeValue("n");
        String position = archIdentifierElement.getAttributeValue("position");
        String type = archIdentifierElement.getAttributeValue("type");

        initAttributes(facs, id, lang, n, position, type);

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
    public Optional<String> getLang() {
        return lang;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    @NotNull
    public Optional<String> getPosition() {
        return position;
    }

    @NotNull
    public Optional<Ref> getRef() {

        Optional<Ref> result = Optional.empty();

        Elements elements = getChildElements("ref", CEI_URI);

        if (elements.size() != 0) {
            Element ref = elements.get(0);
            result = Optional.of(new Ref(ref));
        }

        return result;

    }

    @NotNull
    public Optional<String> getType() {
        return type;
    }

    private void initAttributes(@Nullable String facs, @Nullable String id, @Nullable String lang, @Nullable String n,
                                @Nullable String position, @Nullable String type) {

        if (facs != null && !facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (id != null && !id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (lang != null && !lang.isEmpty()) {
            addAttribute(new Attribute("lang", lang));
            this.lang = Optional.of(lang);
        }

        if (n != null && !n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

        if (position != null && !position.isEmpty()) {
            addAttribute(new Attribute("position", position));
            this.position = Optional.of(position);
        }

        if (type != null && !type.isEmpty()) {
            addAttribute(new Attribute("type", type));
            this.type = Optional.of(type);
        }

    }

}
