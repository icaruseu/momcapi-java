package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.resource.ResourceType;
import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * A representation of the {@code atom:id} XML element as defined by the
 * <a href="http://atomenabled.org/developers/syndication/#requiredFeedElements">Atom developer guidelines</a>.
 * It is used to identify content in the database. The basic construction in MOM-CA is the {@code tag} prefix followed
 * by the content type and a number of parts positioning the document in the content hierarchy, each separated by
 * {@code /}. The parts are separately %-encoded using the methods provided by {@link Util#encode(String)}<br/>
 * <br/>
 * Example in XML:<br/>
 * {@code <atom:id>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1</atom:id>}
 *
 * @author Daniel Jeller
 *         Created on 25.06.2015.
 */
public class AtomId extends Element {

    @NotNull
    private static final String DEFAULT_PREFIX = "tag:www.monasterium.net,2011:";
    private static final int MAX_ID_PARTS = 5;
    private static final int MIN_ID_PARTS_WITHOUT_PREFIX = 2;
    private static final int MIN_ID_PARTS_WITH_PREFIX = 3;
    @NotNull
    private final String id;
    @NotNull
    private final ResourceType type;

    /**
     * Instantiates a new AtomId from an existing {@code atom:id} or by providing the individual parts to use for
     * creation. Doesn't need to include the prefix {@code tag:www.monasterium.net,2011:}. The parts are encoded using
     * {@link Util}.<br/>
     * <br/>
     * Example:<br/>
     * <ul>
     * <li>{@code new AtomId("tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232")}</li>
     * <li>{@code new AtomId("charter", "RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"}</li>
     * <li>{@code new AtomId("tag:www.monasterium.net,2011:", "charter", "RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"}</li>
     * </ul>
     * Result: {@code <atom:id>tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232<atom:id>}
     *
     * @param idParts The id parts to use for the id construction in their correct order.
     */
    AtomId(@NotNull String... idParts) {

        super("atom:id", Namespace.ATOM.getUri());

        if (idParts.length == 1) {
            idParts = splitIntoParts(idParts[0]);
        }

        if (
                (
                        ((idParts.length >= MIN_ID_PARTS_WITH_PREFIX) && idParts[0].equals(DEFAULT_PREFIX))
                                || ((idParts.length >= MIN_ID_PARTS_WITHOUT_PREFIX) && !idParts[0].equals(DEFAULT_PREFIX)))
                        && (idParts.length <= MAX_ID_PARTS)) {

            type = (idParts[0].equals(DEFAULT_PREFIX))
                    ? ResourceType.createFromValue(idParts[1]) : ResourceType.createFromValue(idParts[0]);
            this.id = createIdFromParts(idParts);
            appendChild(this.id);

        } else {
            String message = String.format(
                    "'%s' has not the right amount of parts; probably not a valid atom:id",
                    new Object[]{idParts});
            throw new IllegalArgumentException(message);
        }

    }

    /**
     * @return The AtomId text content, e.g.
     * {@code tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232}.
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * @return The type of the document referenced by the {@code atom:id}, e.g. for
     * {@code tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232} the type
     * is {@code ResourceType.CHARTER}
     */
    @NotNull
    public ResourceType getType() {
        return type;
    }

    @NotNull
    private String createIdFromParts(@NotNull String[] idParts) {

        StringBuilder idBuilder = new StringBuilder(DEFAULT_PREFIX);
        for (String idPart : idParts) {
            if (!idPart.equals(DEFAULT_PREFIX)) {
                idBuilder.append("/");
                idBuilder.append(Util.encode(idPart));
            }
        }
        return idBuilder.toString();

    }

    @NotNull
    private String[] splitIntoParts(@NotNull String id) {
        return id.replace(DEFAULT_PREFIX + "/", "").split("/");
    }

}
