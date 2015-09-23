package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class PlaceName extends AbstractMixedContentElement {

    @NotNull
    private Optional<String> certainty = Optional.empty();
    @NotNull
    private Optional<String> reg = Optional.empty();
    @NotNull
    private Optional<String> type = Optional.empty();

    public PlaceName(@NotNull String content, @NotNull String certainty, @NotNull String reg, @NotNull String type) {

        super(content, "placeName");

        if (!certainty.isEmpty()) {
            addAttribute(new Attribute("certainty", certainty));
            this.certainty = Optional.of(certainty);
        }

        if (!reg.isEmpty()) {
            addAttribute(new Attribute("reg", reg));
            this.reg = Optional.of(reg);
        }

        if (!type.isEmpty()) {
            addAttribute(new Attribute("type", type));
            this.type = Optional.of(type);
        }

    }

    public PlaceName(@NotNull String content) {
        this(content, "", "", "");
    }

    @NotNull
    public Optional<String> getCertainty() {
        return certainty;
    }

    @NotNull
    public Optional<String> getReg() {
        return reg;
    }

    @NotNull
    public Optional<String> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PlaceName{" +
                "certainty=" + certainty +
                ", reg=" + reg +
                ", type=" + type +
                "} " + super.toString();
    }

}
