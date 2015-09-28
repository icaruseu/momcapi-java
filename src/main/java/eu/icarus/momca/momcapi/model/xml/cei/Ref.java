package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by djell on 25/09/2015.
 */
public class Ref extends Element {

    public static final String CEI_URI = Namespace.CEI.getUri();
    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> key = Optional.empty();
    @NotNull
    private Optional<String> lang = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private Optional<String> resp = Optional.empty();
    @NotNull
    private Optional<String> target = Optional.empty();
    @NotNull
    private Optional<String> text = Optional.empty();
    @NotNull
    private Optional<String> type = Optional.empty();

    private Ref() {
        super("cei:ref", CEI_URI);
    }

    public Ref(@NotNull String target) {

        this();

        if (target.isEmpty()) {
            throw new IllegalArgumentException("target is not allowed to be an empty string");
        }

        initMembers("", "", "", "", "", "", "", target, "");

    }

    public Ref(@NotNull String text, @NotNull String facs, @NotNull String id, @NotNull String key, @NotNull String lang, @NotNull String n, @NotNull String resp, @NotNull String target, @NotNull String type) {

        this();

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Text is not allowed to be an empty string");
        }

        initMembers(text, facs, id, key, lang, n, resp, target, type);

    }

    public Ref(@NotNull Element refElement) {

        this();

        if (!Objects.equals(refElement.getLocalName(), "ref") && !refElement.getNamespaceURI().equals(CEI_URI)) {
            throw new IllegalArgumentException("The provided XML Element is not a 'cei:ref' element");
        }

        String text = refElement.getValue();

        String facs = refElement.getAttributeValue("facs");
        String id = refElement.getAttributeValue("id");
        String key = refElement.getAttributeValue("key");
        String lang = refElement.getAttributeValue("lang");
        String n = refElement.getAttributeValue("n");
        String resp = refElement.getAttributeValue("resp");
        String target = refElement.getAttributeValue("target");
        String type = refElement.getAttributeValue("type");

        initMembers(text, facs, id, key, lang, n, resp, target, type);

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
    public Optional<String> getKey() {
        return key;
    }

    @NotNull
    public Optional<String> getLang() {
        return lang;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    @NotNull
    public Optional<String> getResp() {
        return resp;
    }

    @NotNull
    public Optional<String> getTarget() {
        return target;
    }

    @NotNull
    public Optional<String> getText() {
        return text;
    }

    @NotNull
    public Optional<String> getType() {
        return type;
    }

    private void initMembers(String text,
                             String facs,
                             String id,
                             String key,
                             String lang,
                             String n,
                             String resp,
                             String target,
                             String type) {

        if (text != null && !text.isEmpty()) {
            appendChild(text);
            this.text = Optional.of(text);
        }

        if (facs != null && !facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (id != null && !id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (key != null && !key.isEmpty()) {
            addAttribute(new Attribute("key", key));
            this.key = Optional.of(key);
        }

        if (lang != null && !lang.isEmpty()) {
            addAttribute(new Attribute("lang", lang));
            this.lang = Optional.of(lang);
        }

        if (n != null && !n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

        if (resp != null && !resp.isEmpty()) {
            addAttribute(new Attribute("resp", resp));
            this.resp = Optional.of(resp);
        }

        if (target != null && !target.isEmpty()) {
            addAttribute(new Attribute("target", target));
            this.target = Optional.of(target);
        }

        if (type != null && !type.isEmpty()) {
            addAttribute(new Attribute("type", type));
            this.type = Optional.of(type);
        }

    }

}
