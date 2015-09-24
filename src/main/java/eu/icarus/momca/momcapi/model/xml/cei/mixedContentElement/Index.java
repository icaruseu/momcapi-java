package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Index extends AbstractMixedContentElement {

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

        if (!indexName.isEmpty()) {
            addAttribute(new Attribute("indexName", indexName));
            this.indexName = Optional.of(indexName);
        }

        if (!lemma.isEmpty()) {
            addAttribute(new Attribute("lemma", lemma));
            this.lemma = Optional.of(lemma);
        }

        if (!sublemma.isEmpty()) {
            addAttribute(new Attribute("sublemma", sublemma));
            this.sublemma = Optional.of(sublemma);
        }

        if (!type.isEmpty()) {
            addAttribute(new Attribute("type", type));
            this.type = Optional.of(type);
        }

    }

    public Index(@NotNull String content, @NotNull String indexName, @NotNull String lemma,
                 @NotNull String sublemma, @NotNull String type, @NotNull String id, @NotNull String facs,
                 @NotNull String lang, @NotNull String n) {

        this(content, indexName, lemma, sublemma, type);

        if (!id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (!facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (!lang.isEmpty()) {
            addAttribute(new Attribute("lang", lang));
            this.lang = Optional.of(lang);
        }

        if (!n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

    }

    public Index(@NotNull String content) {
        super(content, "index");
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

}
