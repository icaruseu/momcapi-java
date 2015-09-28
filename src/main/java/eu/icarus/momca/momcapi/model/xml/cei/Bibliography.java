package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Bibl;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Bibliography extends Element {


    @NotNull
    private List<Bibl> entries = new ArrayList<>(0);
    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();

    public Bibliography(@NotNull String localName) {
        super("cei:" + localName, Namespace.CEI.getUri());
    }

    public Bibliography(@NotNull String localName, @NotNull List<Bibl> entries) {
        this(localName);
        initEntries(entries);
    }

    public Bibliography(@NotNull String localName, @NotNull List<Bibl> entries,
                        @NotNull String facs, @NotNull String id, @NotNull String n) {
        this(localName);
        initEntries(entries);
        initAttributes(facs, id, n);
    }


    public Bibliography(@NotNull String localName, @NotNull Element bibliographyElement) {

        this(localName);

        String facs = bibliographyElement.getAttributeValue("facs");
        String id = bibliographyElement.getAttributeValue("id");
        String n = bibliographyElement.getAttributeValue("n");

        initAttributes(facs, id, n);

        Elements biblElements = bibliographyElement.getChildElements();

        List<Bibl> bibls = new ArrayList<>();
        for (int i = 0; i < biblElements.size(); i++) {
            bibls.add(new Bibl(biblElements.get(i)));
        }

        initEntries(bibls);

    }

    @NotNull
    public List<Bibl> getEntries() {
        return entries;
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

    private void initEntries(@NotNull List<Bibl> entries) {
        this.entries = entries;
        this.entries.forEach(this::appendChild);
    }

}
