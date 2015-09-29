package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 12/09/2015.
 */
public class Abstract extends AbstractMixedContentElement {

    private static final String LOCAL_NAME = "abstract";
    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> lang = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();

    public Abstract(@NotNull String content) {
        super(content, LOCAL_NAME);
    }

    public Abstract(@NotNull Element abstractElement) {

        super(initContent(abstractElement, LOCAL_NAME), LOCAL_NAME);

        String facs = abstractElement.getAttributeValue("facs");
        String id = abstractElement.getAttributeValue("id");
        String lang = abstractElement.getAttributeValue("lang");
        String n = abstractElement.getAttributeValue("n");

        initAttributes(facs, id, lang, n);

    }

    public Abstract(@NotNull String content, @NotNull String facs, @NotNull String id, @NotNull String lang, @NotNull String n) {
        this(content);
        initAttributes(facs, id, lang, n);
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

    private void initAttributes(@Nullable String facs, @Nullable String id, @Nullable String lang, @Nullable String n) {

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

    }

}
