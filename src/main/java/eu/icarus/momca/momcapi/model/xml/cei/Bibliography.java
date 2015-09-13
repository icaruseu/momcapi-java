package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by djell on 13/09/2015.
 */
public class Bibliography extends Element {

    List<Bibl> entries = new ArrayList<>(0);

    public Bibliography(@NotNull String localName, @NotNull List<Bibl> entries) {

        super("cei:" + localName, Namespace.CEI.getUri());

        entries.forEach(this::appendChild);

        this.entries.addAll(entries);

    }

    public List<Bibl> getEntries() {
        return entries;
    }

}
