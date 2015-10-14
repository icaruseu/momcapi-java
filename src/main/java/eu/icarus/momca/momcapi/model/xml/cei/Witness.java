package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.*;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 25/09/2015.
 */
public class Witness extends Element {

    public static final String CEI_URI = Namespace.CEI.getUri();
    @NotNull
    private Optional<ArchIdentifier> archIdentifier = Optional.empty();
    @NotNull
    private Optional<Auth> auth = Optional.empty();
    @NotNull
    private List<Figure> figures = new ArrayList<>(0);
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> lang = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private Optional<Nota> nota = Optional.empty();
    @NotNull
    private Optional<PhysicalDesc> physicalDesc = Optional.empty();
    @NotNull
    private Optional<TraditioForm> traditioForm = Optional.empty();

    public Witness() {
        super("cei:witness", CEI_URI);
    }

    public Witness(@Nullable ArchIdentifier archIdentifier, @Nullable Auth auth, @Nullable List<Figure> figures,
                   @NotNull String id, @NotNull String lang, @NotNull String n,
                   @Nullable Nota nota, @Nullable PhysicalDesc physicalDesc, @Nullable TraditioForm traditioForm) {
        this();
        initChilds(archIdentifier, auth, figures, nota, physicalDesc, traditioForm);
        initAttributes(id, lang, n);
    }

    public Witness(@NotNull Element witnessElement) {

        this();

        Element archIdentifierElement = witnessElement.getFirstChildElement("archIdentifier", CEI_URI);
        ArchIdentifier archIdentifier = Util.isEmptyElement(archIdentifierElement)
                ? null : new ArchIdentifier(archIdentifierElement);

        Element authElement = witnessElement.getFirstChildElement("auth", CEI_URI);
        Auth auth = Util.isEmptyElement(authElement)
                ? null : new Auth(authElement);

        Elements figureElements = witnessElement.getChildElements("figure", CEI_URI);
        List<Figure> figures = new ArrayList<>(0);
        for (int i = 0; i < figureElements.size(); i++) {
            figures.add(new Figure(figureElements.get(i)));
        }

        Element notaElement = witnessElement.getFirstChildElement("nota", CEI_URI);
        Nota nota = Util.isEmptyElement(notaElement)
                ? null : new Nota(notaElement);

        Element physicalDescElement = witnessElement.getFirstChildElement("physicalDesc", CEI_URI);
        PhysicalDesc physicalDesc = Util.isEmptyElement(physicalDescElement)
                ? null : new PhysicalDesc(physicalDescElement);

        Element traditioFormElement = witnessElement.getFirstChildElement("traditioForm", CEI_URI);
        TraditioForm traditioForm = Util.isEmptyElement(traditioFormElement)
                ? null : new TraditioForm(traditioFormElement);

        initChilds(archIdentifier, auth, figures, nota, physicalDesc, traditioForm);

        String id = witnessElement.getAttributeValue("id");
        String lang = witnessElement.getAttributeValue("lang");
        String n = witnessElement.getAttributeValue("n");

        initAttributes(id, lang, n);

    }

    @NotNull
    public Optional<ArchIdentifier> getArchIdentifier() {
        return archIdentifier;
    }

    @NotNull
    public Optional<Auth> getAuth() {
        return auth;
    }

    @NotNull
    public List<Figure> getFigures() {
        return figures;
    }

    @NotNull
    public Optional<String> getId() {
        return id;
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
    public Optional<Nota> getNota() {
        return nota;
    }

    @NotNull
    public Optional<PhysicalDesc> getPhysicalDesc() {
        return physicalDesc;
    }

    @NotNull
    public Optional<TraditioForm> getTraditioForm() {
        return traditioForm;
    }

    private void initAttributes(@Nullable String id, @Nullable String lang, @Nullable String n) {

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

    private void initChilds(@Nullable ArchIdentifier archIdentifier, @Nullable Auth auth, @Nullable List<Figure> figures,
                            @Nullable Nota nota, @Nullable PhysicalDesc physicalDesc,
                            @Nullable TraditioForm traditioForm) {

        if (traditioForm != null) {
            this.traditioForm = Optional.of(traditioForm);
            appendChild(traditioForm.copy());
        }

        if (archIdentifier != null) {
            this.archIdentifier = Optional.of(archIdentifier);
            appendChild(archIdentifier.copy());
        }

        if (auth != null) {
            this.auth = Optional.of(auth);
            appendChild(auth.copy());
        }

        if (physicalDesc != null) {
            this.physicalDesc = Optional.of(physicalDesc);
            appendChild(physicalDesc.copy());
        }

        if (nota != null) {
            this.nota = Optional.of(nota);
            appendChild(nota.copy());
        }


        if (figures != null && !figures.isEmpty()) {
            this.figures = figures;
            figures.forEach(this::appendChild);
        }

    }

    public boolean isEmpty() {

        return !archIdentifier.isPresent()
                && !auth.isPresent()
                && figures.isEmpty()
                && !nota.isPresent()
                && !physicalDesc.isPresent()
                && !traditioForm.isPresent();

    }

}
