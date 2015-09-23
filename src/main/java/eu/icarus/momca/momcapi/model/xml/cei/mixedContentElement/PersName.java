package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class PersName extends AbstractMixedContentElement {

    @NotNull
    private Optional<String> certainty = Optional.empty();
    @NotNull
    private Optional<String> reg = Optional.empty();
    @NotNull
    private Optional<String> type = Optional.empty();

    public PersName(@NotNull String content, @NotNull String certainty, @NotNull String reg, @NotNull String type) {

        super(content, "persName");

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

    public PersName(@NotNull String content) {
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

}
