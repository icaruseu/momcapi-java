package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

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
    private final String id;
    @NotNull
    private final String text;


    /**
     * Instantiates a new Idno.
     *
     * @param id   The {@code cei:idno/@id}-attribute.
     * @param text The text content.
     */
    public Idno(@NotNull String id, @NotNull String text) {

        super("cei:idno", Namespace.CEI.getUri());
        addAttribute(new Attribute("id", id));
        appendChild(text);

        this.text = text;
        this.id = id;

    }

    /**
     * @return The value of the {@code cei:idno/@id}-attribute.
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * @return The text content.
     */
    @NotNull
    public String getText() {
        return text;
    }

}
