package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class PersName extends AbstractMixedContentElement {

    public static final String LOCAL_NAME = "persName";
    @NotNull
    private Optional<String> certainty = Optional.empty();
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
    @NotNull
    private Optional<String> reg = Optional.empty();
    @NotNull
    private Optional<String> type = Optional.empty();

    public PersName(@NotNull String content, @NotNull String certainty, @NotNull String key, @NotNull String reg,
                    @NotNull String type) {
        this(content);
        initAttributes(certainty, "", "", key, "", "", reg, type);
    }

    public PersName(@NotNull String content, @NotNull String certainty, @NotNull String facs, @NotNull String id,
                    @NotNull String key, @NotNull String lang, @NotNull String n, @NotNull String reg,
                    @NotNull String type) {
        this(content);
        initAttributes(certainty, facs, id, key, lang, n, reg, type);
    }

    public PersName(@NotNull Element persNameElement) {

        this(initContent(persNameElement, LOCAL_NAME));

        String certainty = persNameElement.getAttributeValue("certainty");
        String facs = persNameElement.getAttributeValue("facs");
        String id = persNameElement.getAttributeValue("id");
        String key = persNameElement.getAttributeValue("key");
        String lang = persNameElement.getAttributeValue("lang");
        String n = persNameElement.getAttributeValue("n");
        String reg = persNameElement.getAttributeValue("reg");
        String type = persNameElement.getAttributeValue("type");

        initAttributes(certainty, facs, id, key, lang, n, reg, type);

    }

    public PersName(@NotNull String content) {
        super(content, LOCAL_NAME);
    }

    @NotNull
    public Optional<String> getCertainty() {
        return certainty;
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

    @NotNull
    public Optional<String> getReg() {
        return reg;
    }

    @NotNull
    public Optional<String> getType() {
        return type;
    }

    private void initAttributes(@Nullable String certainty, @Nullable String facs, @Nullable String id,
                                @Nullable String key, @Nullable String lang, @Nullable String n, @Nullable String reg,
                                @Nullable String type) {

        if (certainty != null && !certainty.isEmpty()) {
            addAttribute(new Attribute("certainty", certainty));
            this.certainty = Optional.of(certainty);
        }

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

        if (reg != null && !reg.isEmpty()) {
            addAttribute(new Attribute("reg", reg));
            this.reg = Optional.of(reg);
        }

        if (type != null && !type.isEmpty()) {
            addAttribute(new Attribute("type", type));
            this.type = Optional.of(type);
        }

    }

}
