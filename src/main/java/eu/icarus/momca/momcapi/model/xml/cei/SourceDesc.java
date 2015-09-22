package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Bibl;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by djell on 10/09/2015.
 */
public class SourceDesc extends Element {

    public static final String CEI_URI = Namespace.CEI.getUri();
    public static final String SOURCE_DESC_REGEST = "sourceDescRegest";
    public static final String SOURCE_DESC_VOLLTEXT = "sourceDescVolltext";
    @NotNull
    private Bibliography bibliographyAbstract;
    @NotNull
    private Bibliography bibliographyTenor;

    public SourceDesc() {
        this(new ArrayList<>(0), new ArrayList<>(0));
    }

    public SourceDesc(@NotNull List<String> abstractBiblEntries, @NotNull List<String> tenorBiblEntries) {

        super("cei:sourceDesc", CEI_URI);

        bibliographyAbstract = initBibliography(SOURCE_DESC_REGEST, abstractBiblEntries);
        bibliographyTenor = initBibliography(SOURCE_DESC_VOLLTEXT, tenorBiblEntries);

        appendChild(bibliographyAbstract);
        appendChild(bibliographyTenor);

    }

    @NotNull
    public Bibliography getBibliographyAbstract() {
        return bibliographyAbstract;
    }

    @NotNull
    public Bibliography getBibliographyTenor() {
        return bibliographyTenor;
    }

    private Bibliography initBibliography(@NotNull String bibliographyName, @Nullable List<String> entries) {

        List<Bibl> list;
        if (entries == null || entries.isEmpty()) {
            list = new ArrayList<>(1);
            list.add(new Bibl());
        } else {
            list = entries.stream().map(Bibl::new).collect(Collectors.toList());
        }

        return new Bibliography(bibliographyName, list);

    }

}
