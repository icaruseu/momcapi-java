package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.cei.Ref;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 25/09/2015.
 */
public class ArchIdentifier extends AbstractMixedContentElement {

    public static final String CEI_URI = Namespace.CEI.getUri();

    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> lang = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();


    public ArchIdentifier(@NotNull String content) {
        super(content, "archIdentifier");
    }

    public ArchIdentifier(@NotNull String content,
                          @NotNull String facs, @NotNull String id, @NotNull String lang, @NotNull String n) {

        this(content);

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
    public Optional<String> getLang() {
        return lang;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
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

}
