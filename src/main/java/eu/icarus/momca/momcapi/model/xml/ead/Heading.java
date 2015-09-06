package eu.icarus.momca.momcapi.model.xml.ead;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 06/09/2015.
 */
public class Heading extends Element {

    @NotNull
    private final Optional<String> text;

    public Heading(@Nullable String text) {

        super("ead:head", Namespace.EAD.getUri());

        if (text != null && !text.isEmpty()) {

            this.appendChild(text);
            this.text = Optional.of(text);

        } else {

            this.text = Optional.empty();

        }

    }

    @NotNull
    public Optional<String> getText() {
        return text;
    }

}
