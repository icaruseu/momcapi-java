package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 13/09/2015.
 */
public class Bibl extends Element {

    @NotNull
    private String text;

    public Bibl(@NotNull String text) {

        super("cei:bibl", Namespace.CEI.getUri());

        appendChild(text);

        this.text = text;

    }

    @NotNull
    public String getText() {
        return text;
    }

}
