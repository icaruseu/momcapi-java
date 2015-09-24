package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Note extends AbstractMixedContentElement {

    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private Optional<String> place = Optional.empty();

    public Note(@NotNull String content) {
        super(content, "note");
    }

    public Note(@NotNull String content, @NotNull String place) {

        this(content);

        if (!place.isEmpty()) {
            addAttribute(new Attribute("place", place));
            this.place = Optional.of(place);
        }

    }

    public Note(@NotNull String content, @NotNull String place, @NotNull String id, @NotNull String n) {

        this(content, place);

        if (!id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (!n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

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

}
