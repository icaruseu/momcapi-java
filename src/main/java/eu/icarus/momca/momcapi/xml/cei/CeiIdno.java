package eu.icarus.momca.momcapi.xml.cei;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@code cei:idno}-Element as defined in the
 * <a href="http://www.cei.lmu.de/element.php?ID=19">CEI-Schema</a>.<br/>
 * <br/>
 * Example:<br/>
 * {@code <cei:idno id="KAE_Urkunde_Nr_1" old="1">KAE, Urkunde Nr. 1</cei:idno>}
 *
 * @author Daniel Jeller
 *         Created on 09.07.2015.
 */
public class CeiIdno extends Element {

    @NotNull
    private final String id;
    @NotNull
    private final String text;


    /**
     * Instantiates a new CeiIdno.
     *
     * @param id   The {@code cei:idno/@id}-attribute.
     * @param text The text content.
     */
    public CeiIdno(@NotNull String id, @NotNull String text) {

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

    @NotNull
    @Override
    public String toString() {

        return "CeiIdno{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                "} " + super.toString();

    }

}
