package eu.icarus.momca.momcapi.model.xml.cei;

import nu.xom.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Note extends MixedContentElement {

    @NotNull
    private Optional<String> place = Optional.empty();

    Note(@NotNull String content) {
        this(content, "");
    }

    Note(@NotNull String content, @NotNull String place) {
        super(content, "note");

        if (!place.isEmpty()) {
            addAttribute(new Attribute("place", place));
            this.place = Optional.of(place);
        }

    }

    @NotNull
    public Optional<String> getPlace() {
        return place;
    }

}
