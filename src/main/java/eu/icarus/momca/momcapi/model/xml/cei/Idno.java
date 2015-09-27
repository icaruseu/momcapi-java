package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private Idno() {
        super("cei:idno", Namespace.CEI.getUri());
    }

    public Idno(@NotNull String id, @NotNull String text) {

        this();

        initText(text);
        initAttributes("", id, "", "");

    }

    public Idno(@NotNull String text, @NotNull String facs, @NotNull String id, @NotNull String n, @NotNull String old) {

        this();

        initText(text);
        initAttributes(facs, id, n, old);

    }

    public Idno(@NotNull Element idnoElement) {

        this();

        if (!idnoElement.getLocalName().equals("idno")) {
            String message = String.format("The provided element is '%s' instead of 'cei:idno'.",
                    idnoElement.getQualifiedName());
            throw new IllegalArgumentException(message);
        }

        String text = idnoElement.getValue();

        initText(text);

        String facs = idnoElement.getAttributeValue("facs");
        String id = idnoElement.getAttributeValue("id");
        String n = idnoElement.getAttributeValue("n");
        String old = idnoElement.getAttributeValue("old");

        initAttributes(facs, id, n, old);

    }

    @NotNull
    public Optional<String> getFacs() {
        return facs;
    }

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

    @NotNull
    public String getText() {
        return text;
    }

    private void initAttributes(@Nullable String facs, @NotNull String id, @Nullable String n, @Nullable String old) {

        if (facs != null && !facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (id.isEmpty()) {
            throw new IllegalArgumentException("Id is not allowed to be an empty string.");
        }
        this.id = id;
        addAttribute(new Attribute("id", id));

        if (n != null && !n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

        if (old != null && !old.isEmpty()) {
            addAttribute(new Attribute("old", old));
            this.old = Optional.of(old);
        }

    }

    private void initText(String text) {

        if (text.isEmpty()) {
            throw new IllegalArgumentException("The text is not allowed to be an empty string.");
        }

        this.text = text;
        appendChild(text);

    }

}
