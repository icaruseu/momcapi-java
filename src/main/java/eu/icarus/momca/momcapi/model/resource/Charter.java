package eu.icarus.momca.momcapi.model.resource;


import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.Date;
import eu.icarus.momca.momcapi.model.id.IdCharter;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.XmlValidationProblem;
import eu.icarus.momca.momcapi.model.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.model.xml.cei.*;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.*;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by daniel on 25.06.2015.
 */
public class Charter extends AtomResource {

    public static final String CEI_URI = Namespace.CEI.getUri();
    @NotNull
    private List<Note> backDivNotes = new ArrayList<>(0);
    @NotNull
    private List<GeogName> backGeogNames = new ArrayList<>(0);
    @NotNull
    private List<Index> backIndexes = new ArrayList<>(0);
    @NotNull
    private List<PersName> backPersNames = new ArrayList<>(0);
    @NotNull
    private List<PlaceName> backPlaceNames = new ArrayList<>(0);
    @NotNull
    private Optional<Abstract> charterAbstract = Optional.empty();
    @NotNull
    private Optional<String> charterClass = Optional.empty();
    @NotNull
    private CharterStatus charterStatus;
    @NotNull
    private Date date;
    @NotNull
    private Optional<DiplomaticAnalysis> diplomaticAnalysis = Optional.empty();
    @NotNull
    private Idno idno;
    @NotNull
    private Optional<PlaceName> issuedPlace = Optional.empty();
    @NotNull
    private Optional<String> langMom = Optional.empty();
    @NotNull
    private Optional<Bibliography> sourceDescAbstractBibliography = Optional.empty();
    @NotNull
    private Optional<Bibliography> sourceDescTenorBibliography = Optional.empty();
    @NotNull
    private Optional<Tenor> tenor = Optional.empty();
    @NotNull
    private List<Node> unusedFrontNodes = new ArrayList<>(0);
    @NotNull
    private List<XmlValidationProblem> validationProblems = new ArrayList<>(0);
    @NotNull
    private List<Witness> witListPar = new ArrayList<>(0);
    @NotNull
    private Optional<Witness> witnessOrig = Optional.empty();

    public Charter(@NotNull IdCharter id, @NotNull CharterStatus charterStatus, @NotNull User author, @NotNull Date date) {

        super(id, createParentUri(id, charterStatus, author.getId()), createResourceName(id, charterStatus));
        this.charterStatus = charterStatus;
        this.creator = Optional.of(author.getId());
        this.idno = new Idno(id.getIdentifier(), id.getIdentifier());
        this.date = date;

        regenerateXmlContent();

    }

    public Charter(@NotNull ExistResource existResource) {

        super(existResource);

        try {
            validateCei(existResource);
        } catch (@NotNull SAXException | IOException | ParsingException | ParserConfigurationException e) {
            throw new IllegalArgumentException("Failed to validate the resource.", e);
        }

        initCharterFromXml(toDocument().getRootElement());

    }

    private Element createBackXml() {

        Element back = new Element("cei:back", CEI_URI);

        backPersNames.forEach(persName -> back.appendChild(persName.copy()));
        backPlaceNames.forEach(placeName -> back.appendChild(placeName.copy()));
        backGeogNames.forEach(geogName -> back.appendChild(geogName.copy()));
        backIndexes.forEach(index -> back.appendChild(index.copy()));
        if (!backDivNotes.isEmpty()) {
            Element divNotes = new Element("cei:divNotes", CEI_URI);
            backDivNotes.forEach(note -> divNotes.appendChild(note.copy()));
            back.appendChild(divNotes);
        }

        return back;

    }

    private Element createBodyXml() {

        Element body = new Element("cei:body", CEI_URI);

        body.appendChild(idno.copy());

        Element chDesc = new Element("cei:chDesc", CEI_URI);
        body.appendChild(chDesc);

        charterClass.ifPresent(s -> {
            Element e = new Element("cei:class", CEI_URI);
            e.appendChild(s);
            chDesc.appendChild(e);
        });

        charterAbstract.ifPresent(a -> chDesc.appendChild(a.copy()));

        Element issued = new Element("cei:issued", CEI_URI);
        chDesc.appendChild(issued);
        issuedPlace.ifPresent(p -> issued.appendChild(p.copy()));
        issued.appendChild(date.toCeiDate());

        Element witnessOrig = new Element("cei:witnessOrig", CEI_URI);
        chDesc.appendChild(witnessOrig);
        this.witnessOrig.ifPresent(w -> {

            w.getId().ifPresent(s -> witnessOrig.addAttribute(new Attribute("id", s)));
            w.getLang().ifPresent(s -> witnessOrig.addAttribute(new Attribute("lang", s)));
            w.getN().ifPresent(s -> witnessOrig.addAttribute(new Attribute("n", s)));

            Util.getChildNodes(w).forEach(node -> witnessOrig.appendChild(node.copy()));

        });

        Element witListPar = new Element("cei:witListPar", CEI_URI);
        chDesc.appendChild(witListPar);
        this.witListPar.forEach(witness -> witListPar.appendChild(witness.copy()));

        chDesc.appendChild(diplomaticAnalysis.orElse(new DiplomaticAnalysis()));

        langMom.ifPresent(s -> {
            Element e = new Element("cei:lang_MOM", CEI_URI);
            e.appendChild(s);
            chDesc.appendChild(e);
        });

        tenor.ifPresent(t -> body.appendChild(t.copy()));

        return body;

    }

    private Element createFrontXml() {

        Element front = new Element("cei:front", CEI_URI);

        if (sourceDescAbstractBibliography.isPresent() || sourceDescTenorBibliography.isPresent()) {

            Element sourceDesc = new Element("cei:sourceDesc", CEI_URI);
            front.appendChild(sourceDesc);

            sourceDescAbstractBibliography.ifPresent(sourceDesc::appendChild);
            sourceDescTenorBibliography.ifPresent(sourceDesc::appendChild);

        }

        unusedFrontNodes.forEach(element -> front.appendChild(element.copy()));

        return front;

    }

    private static String createParentUri(@NotNull IdCharter idCharter, @NotNull CharterStatus charterStatus, @NotNull IdUser creator) {

        String parentUri = "";

        switch (charterStatus) {

            case PRIVATE:
                parentUri = String.format("%s/%s/%s/%s",
                        ResourceRoot.USER_DATA.getUri(),
                        creator.getIdentifier(),
                        "metadata.charter",
                        idCharter.getHierarchicalUriPart());
                break;

            case IMPORTED:
                parentUri = String.format("%s/%s",
                        ResourceRoot.IMPORTED_ARCHIVAL_CHARTERS.getUri(),
                        idCharter.getHierarchicalUriPart());
                break;

            case PUBLIC:
                parentUri = String.format("%s/%s",
                        ResourceRoot.PUBLIC_CHARTERS.getUri(),
                        idCharter.getHierarchicalUriPart());
                break;

            case SAVED:
                parentUri = ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED.getUri();
                break;

        }

        return parentUri;

    }

    @NotNull
    private static String createResourceName(@NotNull IdCharter id, @NotNull CharterStatus charterStatus) {

        String resourceName;

        switch (charterStatus) {

            case SAVED:
                resourceName = id.getContentXml().getText().replace("/", "#") + ".xml";
                break;
            case PRIVATE:
                resourceName = String.format("%s.%s", id.getIdentifier(), "charter.xml");
                break;
            case PUBLIC:
            case IMPORTED:
            default:
                resourceName = String.format("%s.%s", id.getIdentifier(), "cei.xml");

        }

        return resourceName;

    }

    @NotNull
    public Optional<Abstract> getAbstract() {
        return charterAbstract;
    }

    @NotNull
    public List<Note> getBackDivNotes() {
        return backDivNotes;
    }

    @NotNull
    public List<GeogName> getBackGeogNames() {
        return backGeogNames;
    }

    @NotNull
    public List<Index> getBackIndexes() {
        return backIndexes;
    }

    @NotNull
    public List<PersName> getBackPersNames() {
        return backPersNames;
    }

    @NotNull
    public List<PlaceName> getBackPlaceNames() {
        return backPlaceNames;
    }

    @NotNull
    public Optional<String> getCharterClass() {
        return charterClass;
    }

    @NotNull
    public CharterStatus getCharterStatus() {
        return charterStatus;
    }

    @NotNull
    public Date getDate() {
        return date;
    }

    @NotNull
    public Optional<DiplomaticAnalysis> getDiplomaticAnalysis() {
        return diplomaticAnalysis;
    }

    @NotNull
    @Override
    public IdCharter getId() {
        return (IdCharter) id;
    }

    @NotNull
    public Idno getIdno() {
        return idno;
    }

    @NotNull
    public Optional<PlaceName> getIssuedPlace() {
        return issuedPlace;
    }

    @NotNull
    public Optional<String> getLangMom() {
        return langMom;
    }

    @NotNull
    public Optional<Bibliography> getSourceDescAbstractBibliography() {
        return sourceDescAbstractBibliography;
    }

    @NotNull
    public Optional<Bibliography> getSourceDescTenorBibliography() {
        return sourceDescTenorBibliography;
    }

    @NotNull
    public Optional<Tenor> getTenor() {
        return tenor;
    }

    @NotNull
    public List<XmlValidationProblem> getValidationProblems() {
        return validationProblems;
    }

    @NotNull
    public List<Witness> getWitListPar() {
        return witListPar;
    }

    @NotNull
    public Optional<Witness> getWitnessOrig() {
        return witnessOrig;
    }

    private void initCharterFromXml(Element xml) {

        backDivNotes = Util.queryXmlForNodes(xml, XpathQuery.QUERY_CEI_BACK_NOTE)
                .stream()
                .map(node -> new Note((Element) node))
                .filter(note -> !note.getContent().isEmpty())
                .collect(Collectors.toList());

        backIndexes = Util.queryXmlForNodes(xml, XpathQuery.QUERY_CEI_BACK_INDEX)
                .stream()
                .map(node -> new Index((Element) node))
                .filter(index -> !index.getContent().isEmpty())
                .collect(Collectors.toList());

        backGeogNames = Util.queryXmlForNodes(xml, XpathQuery.QUERY_CEI_BACK_GEOG_NAME)
                .stream()
                .map(node -> new GeogName((Element) node))
                .filter(geogName -> !geogName.getContent().isEmpty())
                .collect(Collectors.toList());

        backPersNames = Util.queryXmlForNodes(xml, XpathQuery.QUERY_CEI_BACK_PERS_NAME)
                .stream()
                .map(node -> new PersName((Element) node))
                .filter(persName -> !persName.getContent().isEmpty())
                .collect(Collectors.toList());

        backPlaceNames = Util.queryXmlForNodes(xml, XpathQuery.QUERY_CEI_BACK_PLACE_NAME)
                .stream()
                .map(node -> new PlaceName((Element) node))
                .filter(placeName -> !placeName.getContent().isEmpty())
                .collect(Collectors.toList());

        charterAbstract = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_ABSTRACT)
                .map(Abstract::new)
                .filter(a -> !a.getContent().isEmpty());

        charterClass = Util.queryXmlForOptionalString(xml, XpathQuery.QUERY_CEI_CLASS);

        charterStatus = initCharterStatus();

        creator = initCreatorFromXml(xml);

        date = initDateFromXml(xml);

        diplomaticAnalysis = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_DIPLOMATIC_ANALYSIS)
                .map(DiplomaticAnalysis::new)
                .filter(d -> !d.getContent().isEmpty());

        idno = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_BODY_IDNO)
                .map(Idno::new)
                .orElseThrow(MomcaException::new);

        issuedPlace = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_ISSUED_PLACE_NAME)
                .map(PlaceName::new)
                .filter(placeName -> !placeName.getContent().isEmpty());

        langMom = Util.queryXmlForOptionalString(xml, XpathQuery.QUERY_CEI_LANG_MOM);

        sourceDescAbstractBibliography = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_SOURCE_DESC_REGEST)
                .map(element -> new Bibliography("sourceDescRegest", element));

        sourceDescTenorBibliography = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_SOURCE_DESC_VOLLTEXT)
                .map(element -> new Bibliography("sourceDescVolltext", element));

        tenor = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_TENOR)
                .map(Tenor::new)
                .filter(t -> !t.getContent().isEmpty());

        unusedFrontNodes = Util.queryXmlForNodes(xml, XpathQuery.QUERY_CEI_FRONT)
                .stream()
                .filter(node -> node instanceof Element)
                .map(node -> ((Element) node))
                .filter(element -> !element.getLocalName().equals("sourceDesc"))
                .collect(Collectors.toList());

        witListPar = Util.queryXmlForNodes(xml, XpathQuery.QUERY_CEI_WITLIST_PAR_WITNESS)
                .stream()
                .map(node -> new Witness((Element) node))
                .filter(witness -> !witness.isEmpty())
                .collect(Collectors.toList());

        witnessOrig = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_WITNESS_ORIG)
                .map(Witness::new)
                .filter(witness -> !witness.isEmpty());

    }

    private CharterStatus initCharterStatus() {

        CharterStatus status;

        if (getParentUri().contains(ResourceRoot.IMPORTED_ARCHIVAL_CHARTERS.getUri())) {
            status = CharterStatus.IMPORTED;
        } else if (getParentUri().contains(ResourceRoot.USER_DATA.getUri())) {
            status = CharterStatus.PRIVATE;
        } else if (getParentUri().contains(ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED.getUri())) {
            status = CharterStatus.SAVED;
        } else {
            status = CharterStatus.PUBLIC;
        }

        return status;

    }

    private Date initDateFromXml(Element xml) {

        Date result;

        Optional<Date> dateExact = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_ISSUED_DATE)
                .map(DateExact::new)
                .filter(DateExact::isValid)
                .map(Date::new);

        Optional<Date> dateRange = Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_ISSUED_DATE_RANGE)
                .map(DateRange::new)
                .filter(DateRange::isValid)
                .map(Date::new);

        if (!dateExact.isPresent() && !dateRange.isPresent()) {
            throw new MomcaException("No valid date present in provided xml!");
        }

        if (dateExact.isPresent() && !dateRange.isPresent()) {
            result = dateExact.get();
        } else {
            result = dateRange.get();
        }

        return result;

    }

    public boolean isValidCei() {
        return validationProblems.isEmpty();
    }

    @Override
    public void regenerateXmlContent() {

        Element cei = new Element("cei:text", CEI_URI);
        cei.addAttribute(new Attribute("type", "charter"));

        Element front = createFrontXml();
        Element body = createBodyXml();
        Element back = createBackXml();

        cei.appendChild(front);
        cei.appendChild(body);
        cei.appendChild(back);

        Util.changeNamespace(cei, Namespace.CEI);

        String published = getPublished();
        String updated = getUpdated();

        setXmlContent(
                new Document(
                        new AtomEntry(
                                getId().getContentXml(),
                                createAtomAuthor(),
                                (published.isEmpty()) ? AtomResource.localTime() : published,
                                (updated.isEmpty()) ? AtomResource.localTime() : updated,
                                cei)));

        try {
            validateCei(this);
        } catch (@NotNull SAXException | IOException | ParsingException | ParserConfigurationException e) {
            throw new IllegalArgumentException("Failed to validate the resource.", e);
        }

    }

    public void setAbstract(@NotNull Abstract charterAbstract) {

        if (charterAbstract.getContent().isEmpty()) {
            this.charterAbstract = Optional.empty();
        } else {
            this.charterAbstract = Optional.of(charterAbstract);
        }

        regenerateXmlContent();

    }

    public void setBackDivNotes(@NotNull List<Note> backDivNotes) {
        this.backDivNotes = backDivNotes;
        regenerateXmlContent();
    }

    public void setBackGeogNames(@NotNull List<GeogName> backGeogNames) {
        this.backGeogNames = backGeogNames;
        regenerateXmlContent();
    }

    public void setBackIndexes(@NotNull List<Index> backIndexes) {
        this.backIndexes = backIndexes;
        regenerateXmlContent();
    }

    public void setBackPersNames(@NotNull List<PersName> backPersNames) {
        this.backPersNames = backPersNames;
        regenerateXmlContent();
    }

    public void setBackPlaceNames(@NotNull List<PlaceName> backPlaceNames) {
        this.backPlaceNames = backPlaceNames;
        regenerateXmlContent();
    }

    public void setCharterClass(@NotNull String charterClass) {

        if (charterClass.isEmpty()) {
            this.charterClass = Optional.empty();
        } else {
            this.charterClass = Optional.of(charterClass);
        }

        regenerateXmlContent();

    }

    public void setCharterStatus(@NotNull CharterStatus charterStatus) {
        this.charterStatus = charterStatus;
        setParentUri(createParentUri(getId(), charterStatus, creator.get()));
        setResourceName(createResourceName(getId(), charterStatus));
    }

    public void setDate(@NotNull Date date) {
        this.date = date;
        regenerateXmlContent();
    }

    public void setDiplomaticAnalysis(@Nullable DiplomaticAnalysis diplomaticAnalysis) {

        if (diplomaticAnalysis == null) {
            this.diplomaticAnalysis = Optional.empty();
        } else {
            this.diplomaticAnalysis = Optional.of(diplomaticAnalysis);
        }


        regenerateXmlContent();

    }

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string");
        }

        if (getId().isInFond()) {
            IdFond idFond = getId().getIdFond().get();
            id = new IdCharter(idFond.getIdArchive().getIdentifier(), idFond.getIdentifier(), identifier);
        } else {
            IdCollection idCollection = getId().getIdCollection().get();
            id = new IdCharter(idCollection.getIdentifier(), identifier);
        }

        setParentUri(createParentUri(getId(), charterStatus, getCreator().get()));
        setResourceName(createResourceName(getId(), charterStatus));

        regenerateXmlContent();

    }

    public void setIdno(@NotNull Idno idno) {
        this.idno = idno;
        regenerateXmlContent();
    }

    public void setIssuedPlace(@NotNull PlaceName issuedPlace) {

        if (issuedPlace.getContent().isEmpty()) {
            this.issuedPlace = Optional.empty();
        } else {

            this.issuedPlace = Optional.of(issuedPlace);
        }

        regenerateXmlContent();

    }

    public void setLangMom(@NotNull String langMom) {

        if (langMom.isEmpty()) {
            this.langMom = Optional.empty();
        } else {
            this.langMom = Optional.of(langMom);
        }

        regenerateXmlContent();

    }

    public void setSourceDescAbstractBibliography(@NotNull List<Bibl> bibliographyItems) {

        if (bibliographyItems.isEmpty()) {
            this.sourceDescAbstractBibliography = Optional.empty();
        } else {
            this.sourceDescAbstractBibliography = Optional.of(new Bibliography("sourceDescRegest", bibliographyItems));
        }

        regenerateXmlContent();

    }

    public void setSourceDescTenorBibliography(@NotNull List<Bibl> bibliographyItems) {

        if (bibliographyItems.isEmpty()) {
            this.sourceDescTenorBibliography = Optional.empty();
        } else {
            this.sourceDescTenorBibliography = Optional.of(new Bibliography("sourceDescVolltext", bibliographyItems));
        }

        regenerateXmlContent();

    }

    public void setTenor(@NotNull Tenor tenor) {

        if (tenor.getContent().isEmpty()) {
            this.tenor = Optional.empty();
        } else {
            this.tenor = Optional.of(tenor);
        }

        regenerateXmlContent();

    }

    public void setWitListPar(@NotNull List<Witness> witListPar) {

        this.witListPar = witListPar
                .stream()
                .filter(witness -> !witness.isEmpty())
                .collect(Collectors.toList());

        regenerateXmlContent();

    }

    public void setWitnessOrig(@Nullable Witness witnessOrig) {

        if (witnessOrig == null || witnessOrig.isEmpty()) {
            this.witnessOrig = Optional.empty();
        } else {
            this.witnessOrig = Optional.of(witnessOrig);
        }

        regenerateXmlContent();

    }

    @NotNull
    public Element toCei() {
        return getContent();
    }

    private void validateCei(@NotNull ExistResource resource)
            throws SAXException, ParserConfigurationException, ParsingException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        factory.setSchema(schemaFactory.newSchema(new Source[]{
                new StreamSource(this.getClass().getResourceAsStream("/cei.xsd"))}));

        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new SimpleErrorHandler());

        Builder builder = new Builder(reader);
        builder.build(toCei().toXML(), CEI_URI);

    }

    private class SimpleErrorHandler implements ErrorHandler {

        private void addToXmlValidationProblem(@NotNull XmlValidationProblem.SeverityLevel severityLevel, @NotNull SAXParseException e) {
            validationProblems.add(new XmlValidationProblem(severityLevel, e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
        }

        public void error(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.SeverityLevel.ERROR, e);
        }

        public void fatalError(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.SeverityLevel.FATAL_ERROR, e);
        }

        public void warning(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.SeverityLevel.WARNING, e);
        }

    }

}
