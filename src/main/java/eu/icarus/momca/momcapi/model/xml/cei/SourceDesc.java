package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Bibl;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 10/09/2015.
 */
public class SourceDesc extends Element {

    public static final String CEI_URI = Namespace.CEI.getUri();
    @NotNull
    private Optional<Bibliography> bibliographyAbstract = Optional.empty();
    @NotNull
    private Optional<Bibliography> bibliographyTenor = Optional.empty();

    public SourceDesc() {
        super("cei:sourceDesc", CEI_URI);
    }

    public SourceDesc(@Nullable List<String> abstractBiblEntries, @Nullable List<String> tenorBiblEntries) {

        this();

        if (abstractBiblEntries != null && !abstractBiblEntries.isEmpty()) {
            bibliographyAbstract = initBibliography("sourceDescRegest", abstractBiblEntries);
            appendChild(bibliographyAbstract.get());
        }

        if (tenorBiblEntries != null && !tenorBiblEntries.isEmpty()) {
            bibliographyTenor = initBibliography("sourceDescVolltext", tenorBiblEntries);
            appendChild(bibliographyTenor.get());
        }

    }

    @NotNull
    public Optional<Bibliography> getBibliographyAbstract() {
        return bibliographyAbstract;
    }

    @NotNull
    public Optional<Bibliography> getBibliographyTenor() {
        return bibliographyTenor;
    }

    private Optional<Bibliography> initBibliography(@NotNull String bibliographyName, @NotNull List<String> entries) {

        List<Bibl> list = entries.stream().map(Bibl::new).collect(Collectors.toList());
        Bibliography bibliography = new Bibliography(bibliographyName, list);

        return Optional.of(bibliography);

    }

}
