package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 25/09/2015.
 */
public class Rubrum extends AbstractMixedContentElement {

    public static final String CEI_URI = Namespace.CEI.getUri();
    public static final String LOCAL_NAME = "rubrum";

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

    public Rubrum(@NotNull String content) {
        super(content, LOCAL_NAME);
    }

    public Rubrum(@NotNull String content,
                  @NotNull String facs, @NotNull String id, @NotNull String lang, @NotNull String n,
                  @NotNull String position, @NotNull String type) {
        this(content);
        initAttributes(facs, id, lang, n, position, type);
    }

    public Rubrum(@NotNull Element rubrumElement) {

        this(initContent(rubrumElement, LOCAL_NAME));

        String facs = rubrumElement.getAttributeValue("facs");
        String id = rubrumElement.getAttributeValue("id");
        String lang = rubrumElement.getAttributeValue("lang");
        String n = rubrumElement.getAttributeValue("n");
        String position = rubrumElement.getAttributeValue("position");
        String type = rubrumElement.getAttributeValue("type");

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
