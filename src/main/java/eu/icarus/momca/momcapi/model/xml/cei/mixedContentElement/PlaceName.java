package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class PlaceName extends AbstractMixedContentElement {
    @NotNull
    private Optional<String> certainty = Optional.empty();
    @NotNull
    private Optional<String> existent = Optional.empty();
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

    public PlaceName(@NotNull String content) {
        super(content, "placeName");
    }

    public PlaceName(@NotNull String content, @NotNull String certainty, @NotNull String reg, @NotNull String type,
                     @NotNull String existent, @NotNull String key) {

        this(content);

        if (!certainty.isEmpty()) {
            addAttribute(new Attribute("certainty", certainty));
            this.certainty = Optional.of(certainty);
        }

        if (!reg.isEmpty()) {
            addAttribute(new Attribute("reg", reg));
            this.reg = Optional.of(reg);
        }

        if (!type.isEmpty()) {
            addAttribute(new Attribute("type", type));
            this.type = Optional.of(type);
        }

        if (!existent.isEmpty()) {
            addAttribute(new Attribute("existent", existent));
            this.existent = Optional.of(existent);
        }

        if (!key.isEmpty()) {
            addAttribute(new Attribute("key", key));
            this.key = Optional.of(key);
        }

    }

    public PlaceName(@NotNull String content, @NotNull String certainty, @NotNull String reg, @NotNull String type,
                     @NotNull String existent, @NotNull String key, @NotNull String facs, @NotNull String id,
                     @NotNull String lang, @NotNull String n) {

        this(content, certainty, reg, type, existent, key);

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
    public Optional<String> getCertainty() {
        return certainty;
    }

    @NotNull
    public Optional<String> getExistent() {
        return existent;
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

}
