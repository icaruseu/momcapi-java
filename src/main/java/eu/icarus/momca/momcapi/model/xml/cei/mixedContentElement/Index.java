package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import nu.xom.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 13/09/2015.
 */
public class Index extends AbstractMixedContentElement {

    @NotNull
    private Optional<String> indexName = Optional.empty();
    @NotNull
    private Optional<String> lemma = Optional.empty();
    @NotNull
    private Optional<String> sublemma = Optional.empty();

    Index(@NotNull String content, @NotNull String indexName, @NotNull String lemma, @NotNull String sublemma) {

        super(content, "index");

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

    }

    Index(@NotNull String content) {
        this(content, "", "", "");
    }

    @NotNull
    public Optional<String> getIndexName() {
        return indexName;
    }

    @NotNull
    public Optional<String> getLemma() {
        return lemma;
    }

    @NotNull
    public Optional<String> getSublemma() {
        return sublemma;
    }

}
