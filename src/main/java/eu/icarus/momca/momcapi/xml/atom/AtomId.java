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
    public static final String DEFAULT_PREFIX = "tag:www.monasterium.net,2011:";
    private static final int MAX_ID_PARTS = 5;
    private static final int MIN_ID_PARTS_WITHOUT_PREFIX = 2;
    private static final int MIN_ID_PARTS = 3;
    @NotNull
    private final String text;
    @NotNull
    private final ResourceType type;

   public AtomId(@NotNull String text) {

        super("atom:id", Namespace.ATOM.getUri());

        String[] idParts = text.split("/");

        if (idParts.length < MIN_ID_PARTS || idParts.length > MAX_ID_PARTS) {
            throw new IllegalArgumentException(String.format("'%s' is not a valid atom:id text.", text));
        }

        if (isMissingPrefix(idParts)) {
            throw new IllegalArgumentException(String.format("The prefix '%s' is missing from the provided ", DEFAULT_PREFIX));
        }

        type = ResourceType.createFromValue(idParts[1]);

        if (hasWrongNumberOfPartsForType(idParts.length, type)) {
            String message = String.format("'%s' doesn't have the correct number of parts, between '%d' and '%d'.",
                    text, type.getMinIdParts(), type.getMaxIdParts());
            throw new IllegalArgumentException(message);
        }

        this.text = text;
        appendChild(text);

    }

    private boolean hasWrongNumberOfPartsForType(int numberOfParts, @NotNull ResourceType type) {
        return numberOfParts < type.getMinIdParts() || numberOfParts > type.getMaxIdParts();
    }

    private boolean isMissingPrefix(@NotNull String... idParts) {
        return !idParts[0].equals(DEFAULT_PREFIX);
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

    /**
     * @return The AtomId text content, e.g.
     * {@code tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232}.
     */
    @NotNull
    public String getText() {
        return text;
    }

}
