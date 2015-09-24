package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Bibl;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by djell on 13/09/2015.
 */
public class Bibliography extends Element {

    @NotNull
    private List<Bibl> entries;

    public Bibliography(@NotNull String localName, @NotNull List<Bibl> entries) {
        super("cei:" + localName, Namespace.CEI.getUri());
        this.entries = entries;
        this.entries.forEach(this::appendChild);
    }

    @NotNull
    public List<Bibl> getEntries() {
        return entries;
    }

}
