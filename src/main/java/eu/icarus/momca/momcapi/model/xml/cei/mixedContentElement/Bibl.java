package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Bibl extends AbstractMixedContentElement {

    public static final String LOCAL_NAME = "bibl";
    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> key = Optional.empty();
    @NotNull
    private Optional<String> lang = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();

    public Bibl(@NotNull String content) {
        super(content, LOCAL_NAME);
    }

    public Bibl(@NotNull String content, @NotNull String facs, @NotNull String id, @NotNull String key,
                @NotNull String lang, @NotNull String n) {
        this(content);
        initAttributes(facs, id, key, lang, n);
    }

    public Bibl(@NotNull Element biblElement) {

        this(initContent(biblElement, LOCAL_NAME));

        String facs = biblElement.getAttributeValue("facs");
        String id = biblElement.getAttributeValue("id");
        String key = biblElement.getAttributeValue("key");
        String lang = biblElement.getAttributeValue("lang");
        String n = biblElement.getAttributeValue("n");

        initAttributes(facs, id, key, lang, n);

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
    public Optional<String> getKey() {
        return key;
    }

    @NotNull
    public Optional<String> getLang() {
        return lang;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    private void initAttributes(@Nullable String facs, @Nullable String id, @Nullable String key,
                                @Nullable String lang, @Nullable String n) {

        if (facs != null && !facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (id != null && !id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (key != null && !key.isEmpty()) {
            addAttribute(new Attribute("key", key));
            this.key = Optional.of(key);
        }

        if (lang != null && !lang.isEmpty()) {
            addAttribute(new Attribute("lang", lang));
            this.lang = Optional.of(lang);
        }

        if (n != null && !n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

    }


}
