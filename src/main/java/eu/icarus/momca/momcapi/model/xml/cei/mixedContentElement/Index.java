package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Index extends AbstractMixedContentElement {

    public static final String LOCAL_NAME = "index";
    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> indexName = Optional.empty();
    @NotNull
    private Optional<String> lang = Optional.empty();
    @NotNull
    private Optional<String> lemma = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private Optional<String> sublemma = Optional.empty();
    @NotNull
    private Optional<String> type = Optional.empty();

    public Index(@NotNull String content, @NotNull String indexName, @NotNull String lemma,
                 @NotNull String sublemma, @NotNull String type) {
        this(content);
        initAttributes("", "", indexName, "", lemma, "", sublemma, type);
    }

    public Index(@NotNull String content, @NotNull String indexName, @NotNull String lemma,
                 @NotNull String sublemma, @NotNull String type, @NotNull String id, @NotNull String facs,
                 @NotNull String lang, @NotNull String n) {
        this(content, indexName, lemma, sublemma, type);
        initAttributes(facs, id, indexName, lang, lemma, n, sublemma, type);
    }

    public Index(@NotNull Element indexElement) {

        this(initContent(indexElement, LOCAL_NAME));

        String facs = indexElement.getAttributeValue("facs");
        String id = indexElement.getAttributeValue("id");
        String indexName = indexElement.getAttributeValue("indexName");
        String lang = indexElement.getAttributeValue("lang");
        String lemma = indexElement.getAttributeValue("lemma");
        String n = indexElement.getAttributeValue("n");
        String sublemma = indexElement.getAttributeValue("sublemma");
        String type = indexElement.getAttributeValue("type");

        initAttributes(facs, id, indexName, lang, lemma, n, sublemma, type);

    }

    public Index(@NotNull String content) {
        super(content, LOCAL_NAME);
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
    public Optional<String> getIndexName() {
        return indexName;
    }

    @NotNull
    public Optional<String> getLang() {
        return lang;
    }

    @NotNull
    public Optional<String> getLemma() {
        return lemma;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    @NotNull
    public Optional<String> getSublemma() {
        return sublemma;
    }

    @NotNull
    public Optional<String> getType() {
        return type;
    }

    private void initAttributes(@Nullable String facs, @Nullable String id, @Nullable String indexName,
                                @Nullable String lang, @Nullable String lemma, @Nullable String n,
                                @Nullable String sublemma, @Nullable String type) {

        if (facs != null && !facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (id != null && !id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (indexName != null && !indexName.isEmpty()) {
            addAttribute(new Attribute("indexName", indexName));
            this.indexName = Optional.of(indexName);
        }

        if (lang != null && !lang.isEmpty()) {
            addAttribute(new Attribute("lang", lang));
            this.lang = Optional.of(lang);
        }

        if (lemma != null && !lemma.isEmpty()) {
            addAttribute(new Attribute("lemma", lemma));
            this.lemma = Optional.of(lemma);
        }

        if (n != null && !n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

        if (sublemma != null && !sublemma.isEmpty()) {
            addAttribute(new Attribute("sublemma", sublemma));
            this.sublemma = Optional.of(sublemma);
        }

        if (type != null && !type.isEmpty()) {
            addAttribute(new Attribute("type", type));
            this.type = Optional.of(type);
        }

    }

}
