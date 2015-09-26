package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Note extends AbstractMixedContentElement {

    public static final String LOCAL_NAME = "note";
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private Optional<String> place = Optional.empty();

    public Note(@NotNull String content) {
        super(content, LOCAL_NAME);
    }

    public Note(@NotNull String content, @NotNull String place) {
        this(content);
        initAttributes(place, "", "");
    }

    public Note(@NotNull String content, @NotNull String place, @NotNull String id, @NotNull String n) {
        this(content);
        initAttributes(place, id, n);
    }

    public Note(@NotNull Element noteElement) {

        this(initContent(noteElement, LOCAL_NAME));

        String place = noteElement.getAttributeValue("place");
        String id = noteElement.getAttributeValue("id");
        String n = noteElement.getAttributeValue("n");

        initAttributes(place, id, n);

    }

    @NotNull
    public Optional<String> getId() {
        return id;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    @NotNull
    public Optional<String> getPlace() {
        return place;
    }

    private void initAttributes(@Nullable String place, @Nullable String id, @Nullable String n) {

        if (place != null && !place.isEmpty()) {
            addAttribute(new Attribute("place", place));
            this.place = Optional.of(place);
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
