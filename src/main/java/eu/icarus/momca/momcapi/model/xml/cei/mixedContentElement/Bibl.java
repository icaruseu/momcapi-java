package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Bibl extends AbstractMixedContentElement {

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
        super(content, "bibl");
    }

    public Bibl(@NotNull String content, @NotNull String key, @NotNull String facs, @NotNull String id, @NotNull String lang, @NotNull String n) {

        this(content);

        if (!key.isEmpty()) {
            addAttribute(new Attribute("key", key));
            this.key = Optional.of(key);
        }

        if (!facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (!id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (!lang.isEmpty()) {
            addAttribute(new Attribute("lang", lang));
            this.lang = Optional.of(lang);
        }

        if (!n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

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


}
