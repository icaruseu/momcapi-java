package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 12/09/2015.
 */
public class FigDesc extends AbstractMixedContentElement {

    public static final String LOCAL_NAME = "figDesc";
    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();

    public FigDesc(@NotNull String content) {
        super(content, LOCAL_NAME);
    }

    public FigDesc(@NotNull String content, @NotNull String facs, @NotNull String id, @NotNull String n) {
        this(content);
        initAttributes(facs, id, n);
    }

    public FigDesc(@NotNull Element figDescElement) {

        this(initContent(figDescElement, LOCAL_NAME));

        String facs = figDescElement.getAttributeValue("facs");
        String id = figDescElement.getAttributeValue("id");
        String n = figDescElement.getAttributeValue("n");

        initAttributes(facs, id, n);

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

    private void initAttributes(@Nullable String facs, @Nullable String id, @Nullable String n) {

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

    }

}
