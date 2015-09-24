package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents a {@code cei:idno}-Element as defined in the
 * <a href="http://www.cei.lmu.de/element.php?ID=19">CEI-Schema</a>.<br/> This information represents the concept of a
 * {@code signature} used to identify the charter in it's archival context.
 * <br/>
 * Example:<br/>
 * {@code <cei:idno id="KAE_Urkunde_Nr_1" old="1">KAE, Urkunde Nr. 1</cei:idno>}
 *
 * @author Daniel Jeller
 *         Created on 09.07.2015.
 */
public class Idno extends Element {

    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private String id;
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private Optional<String> old = Optional.empty();
    @NotNull
    private String text;

    public Idno(@NotNull String id, @NotNull String text) {

        super("cei:idno", Namespace.CEI.getUri());

        this.text = text;
        this.id = id;

        appendChild(text);
        addAttribute(new Attribute("id", id));

    }

    /**
     * Instantiates a new Idno.
     *
     * @param id   The {@code cei:idno/@id}-attribute.
     * @param text The text content.
     */
    public Idno(@NotNull String id, @NotNull String text, @NotNull String old, @NotNull String facs, @NotNull String n) {

        this(id, text);

        if (!old.isEmpty()) {
            addAttribute(new Attribute("old", old));
            this.old = Optional.of(old);
        }

        if (!facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (!n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }


    }

    @NotNull
    public Optional<String> getFacs() {
        return facs;
    }

    /**
     * @return The value of the {@code cei:idno/@id}-attribute.
     */
    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    @NotNull
    public Optional<String> getOld() {
        return old;
    }

    /**
     * @return The text content.
     */
    @NotNull
    public String getText() {
        return text;
    }

}
