package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents an abstract <a href="http://www.cei.lmu.de/index.php">CEI</a> date that is an XML element with a
 * literal value, e.g. {@code 2nd December 1678} and specifies methods for concrete date classes to implement.
 *
 * @author Daniel Jeller
 *         Created on 10.07.2015.
 */
public abstract class DateAbstract extends Element {

    @NotNull
    private Optional<String> certainty = Optional.empty();
    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> lang = Optional.empty();
    @NotNull
    private String literalDate = "";
    @NotNull
    private Optional<String> n = Optional.empty();

    private DateAbstract(@NotNull String localName) {
        super("cei:" + localName, Namespace.CEI.getUri());
    }

    DateAbstract(@NotNull String localName, @NotNull String literalDate, @NotNull String certainty,
                 @NotNull String facs, @NotNull String id, @NotNull String lang, @NotNull String n) {

        this(localName);

        initLiteralDate(literalDate);
        initAttributes(certainty, facs, id, lang, n);

    }

    DateAbstract(@NotNull String localName, @NotNull Element dateElement) {

        this(localName);

        if (!dateElement.getLocalName().equals(localName)) {
            String message = String.format("The provided element is named '%s' instead of 'cei:%s'.",
                    dateElement.getQualifiedName(), localName);
            throw new IllegalArgumentException(message);
        }

        initLiteralDate(dateElement.getValue());

        String certainty = dateElement.getAttributeValue("certainty");
        String facs = dateElement.getAttributeValue("facs");
        String id = dateElement.getAttributeValue("id");
        String lang = dateElement.getAttributeValue("lang");
        String n = dateElement.getAttributeValue("n");

        initAttributes(certainty, facs, id, lang, n);

    }

    /**
     * @return {@code True} if the date could be converted to the other date type.<br/>
     * </br>
     * Examples:
     * <ul>
     * <li>{@code <cei:date value="17859999">1785</cei:date>} could be
     * {@code <cei:dateRange from="17850101" to="17851231">1785</cei:dateRange>}</li>
     * <li>{@code <cei:dateRange from="17850712" to="17850712">12th July 1785</cei:dateRange>} could be
     * {@code <cei:date value="17850712">12th July 1785</cei:date>}</li>
     * </ul>
     */
    public abstract boolean couldBeOtherDateType();

    @NotNull
    public Optional<String> getCertainty() {
        return certainty;
    }

    @NotNull
    public Optional<String> getFacs() {
        return facs;
    }

    @NotNull
    public Optional<String> getId() {
        return id;
    }

    @NotNull
    public Optional<String> getLang() {
        return lang;
    }

    /**
     * @return The literal date.
     */
    @NotNull
    public String getLiteralDate() {
        return literalDate;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    private void initAttributes(@Nullable String certainty, @Nullable String facs, @Nullable String id,
                                @Nullable String lang, @Nullable String n) {

        if (certainty != null && !certainty.isEmpty()) {
            addAttribute(new Attribute("certainty", certainty));
            this.certainty = Optional.of(certainty);
        }

        if (facs != null && !facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (id != null && !id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (lang != null && !lang.isEmpty()) {
            addAttribute(new Attribute("lang", lang));
            this.lang = Optional.of(lang);
        }

        if (n != null && !n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

    }

    private void initLiteralDate(@NotNull String literalDate) {

        this.literalDate = literalDate;

        if (!literalDate.isEmpty()) {
            this.appendChild(literalDate);
        }

    }

    public abstract boolean isUndated();

    /**
     * @return {@code True} if the date is a valid {@code cei:date}.
     */
    public abstract boolean isValid();

    @Override
    @NotNull
    public String toString() {
        return "DateAbstract{" +
                "literalDate='" + literalDate + '\'' +
                "} " + super.toString();
    }

}
