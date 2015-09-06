package eu.icarus.momca.momcapi.model.xml.ead;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 06/09/2015.
 */
public class Bibliography extends Element {

    private static final String BIBREF_NAME = "ead:bibref";
    private static final String EAD_URI = Namespace.EAD.getUri();
    @NotNull
    private final List<String> entries;
    @NotNull
    private final Optional<Heading> heading;

    public Bibliography() {
        this(null);
    }

    public Bibliography(@Nullable String heading, @NotNull String... entries) {

        super("ead:bibliography", EAD_URI);

        this.heading = createHeading(heading);
        this.entries = Arrays.asList(entries);

        this.heading.ifPresent(this::appendChild);
        this.entries.forEach(this::appendEntryElement);

    }

    private void appendEntryElement(@NotNull String text) {
        Element element = new Element(BIBREF_NAME, EAD_URI);
        element.appendChild(text);
        this.appendChild(element);
    }

    @NotNull
    private Optional<Heading> createHeading(@Nullable String heading) {

        Optional<Heading> result = Optional.empty();

        if (heading != null && !heading.isEmpty()) {
            result = Optional.of(new Heading(heading));
        }

        return result;

    }

    @NotNull
    public List<String> getEntries() {
        return entries;
    }

    @NotNull
    public Optional<Heading> getHeading() {
        return heading;
    }

}
