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
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Abstract;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Bibl;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.PlaceName;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Tenor;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.*;
import org.jetbrains.annotations.NotNull;
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

/**
 * Created by daniel on 25.06.2015.
 */
public class Charter extends AtomResource {

    public static final String CEI_URI = Namespace.CEI.getUri();
    @NotNull
    private final List<XmlValidationProblem> validationProblems = new ArrayList<>(0);
    @NotNull
    private Optional<Abstract> charterAbstract = Optional.empty();
    @NotNull
    private Optional<String> charterClass = Optional.empty();
    @NotNull
    private CharterStatus charterStatus;
    @NotNull
    private Date date;
    @NotNull
    private Element diplomaticAnalysis = new Element("cei:diplomaticAnalysis", CEI_URI);
    @NotNull
    private Idno idno;
    @NotNull
    private Optional<PlaceName> issuedPlace = Optional.empty();
    @NotNull
    private Optional<String> langMom = Optional.empty();
    @NotNull
    private Optional<SourceDesc> sourceDesc = Optional.empty();
    @NotNull
    private Optional<Tenor> tenor = Optional.empty();
    @NotNull
    private List<Node> unusedFrontNodes = new ArrayList<>(0);

    public Charter(@NotNull IdCharter id, @NotNull CharterStatus charterStatus, @NotNull User author, @NotNull Date date) {

        super(id, createParentUri(id, charterStatus, author.getId()), createResourceName(id, charterStatus));
        this.charterStatus = charterStatus;
        this.creator = Optional.of(author.getId());
        this.idno = new Idno(id.getIdentifier(), id.getIdentifier());
        this.date = date;

        updateXmlContent();

    }

    public Charter(@NotNull ExistResource existResource) {

        super(existResource);

        try {
            validateCei(existResource);
        } catch (@NotNull SAXException | IOException | ParsingException | ParserConfigurationException e) {
            throw new IllegalArgumentException("Failed to validate the resource.", e);
        }

        Element xml = toDocument().getRootElement();

        creator = readCreatorFromXml(xml);
        charterStatus = readCharterStatus();
        idno = readIdno(xml);
        date = readDate(xml);

        unusedFrontNodes = readUnusedFrontElements(xml);
        diplomaticAnalysis = readDiplomaticAnalysis(xml);

        sourceDesc = readSourceDesc(xml);
        tenor = readMixedContentElement(xml, XpathQuery.QUERY_CEI_TENOR).map(Tenor::new);
        charterAbstract = readMixedContentElement(xml, XpathQuery.QUERY_CEI_ABSTRACT).map(Abstract::new);
        langMom = Util.queryXmlToOptional(xml, XpathQuery.QUERY_CEI_LANG_MOM);
        charterClass = Util.queryXmlToOptional(xml, XpathQuery.QUERY_CEI_CLASS);
        issuedPlace = readIssuedPlace(xml);

    }

    private static String createParentUri(@NotNull IdCharter idCharter, @NotNull CharterStatus charterStatus, @NotNull IdUser creator) {

        String parentUri = "";

        switch (charterStatus) {

            case PRIVATE:
                parentUri = String.format("%s/%s/%s/%s",
                        ResourceRoot.USER_DATA.getUri(),
                        creator.getIdentifier(),
                        "metadata.charter",
                        idCharter.getIdCollection().get().getIdentifier());
                break;

            case IMPORTED:
                parentUri = String.format("%s/%s",
                        ResourceRoot.IMPORTED_ARCHIVAL_CHARTERS.getUri(),
                        getHierarchicalUriPart(idCharter));
                break;

            case PUBLIC:
                parentUri = String.format("%s/%s",
                        ResourceRoot.PUBLIC_CHARTERS.getUri(),
                        getHierarchicalUriPart(idCharter));
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
    private static String getHierarchicalUriPart(@NotNull IdCharter idCharter) {

        String idPart;

        if (idCharter.isInFond()) {

            String archiveIdentifier = idCharter.getIdFond().get().getIdArchive().getIdentifier();
            String fondIdentifier = idCharter.getIdFond().get().getIdentifier();
            idPart = archiveIdentifier + "/" + fondIdentifier;

        } else {

            idPart = idCharter.getIdCollection().get().getIdentifier();

        }

        return idPart;

    }

    private Element createBackXml() {

        Element back = new Element("cei:back", CEI_URI);

        return back;

    }

    private Element createBodyXml() {

        Element body = new Element("cei:body", CEI_URI);

        body.appendChild(idno.copy());

        Element chDesc = new Element("cei:chDesc", CEI_URI);
        body.appendChild(chDesc);

        Element issued = new Element("cei:issued", CEI_URI);
        chDesc.appendChild(issued);
        issuedPlace.ifPresent(issued::appendChild);
        issued.appendChild(date.toCeiDate());

        charterAbstract.ifPresent(chDesc::appendChild);

        charterClass.ifPresent(s -> {
            Element e = new Element("cei:class", CEI_URI);
            e.appendChild(s);
            chDesc.appendChild(e);
        });

        chDesc.appendChild(diplomaticAnalysis.copy());

        langMom.ifPresent(s -> {
            Element e = new Element("cei:lang_MOM", CEI_URI);
            e.appendChild(s);
            chDesc.appendChild(e);
        });

        tenor.ifPresent(body::appendChild);

        return body;

    }

    private Element createFrontXml() {

        Element front = new Element("cei:front", CEI_URI);

        sourceDesc.ifPresent(front::appendChild);
        unusedFrontNodes.forEach(element -> front.appendChild(element.copy()));

        return front;

    }

    @NotNull
    public Optional<Abstract> getAbstract() {
        return charterAbstract;
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
    public Optional<SourceDesc> getSourceDesc() {
        return sourceDesc;
    }

    @NotNull
    public Optional<Tenor> getTenor() {
        return tenor;
    }

    @NotNull
    public List<XmlValidationProblem> getValidationProblems() {
        return validationProblems;
    }

    public boolean isValidCei() {
        return validationProblems.isEmpty();
    }

    @NotNull
    private List<String> readBiblEntries(Element parentElement, String bibliographyName) {

        List<String> abstractBiblEntries = new ArrayList<>(0);

        Element sourceDescRegestElement = parentElement.getFirstChildElement(bibliographyName, CEI_URI);

        if (sourceDescRegestElement != null) {

            Elements sourceDescRegestBibl = sourceDescRegestElement.getChildElements();

            for (int i = 0; i < sourceDescRegestBibl.size(); i++) {
                Element bibl = sourceDescRegestBibl.get(i);
                abstractBiblEntries.add(bibl.getValue());
            }

        }

        return abstractBiblEntries;

    }

    private CharterStatus readCharterStatus() {

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

    private Date readDate(Element xml) {

        DateAbstract dateCei;

        Nodes dateNodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_ISSUED_DATE);
        Nodes dateRangeNodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_ISSUED_DATE_RANGE);

        if ((dateNodes.size() == 1 && dateRangeNodes.size() == 0) || (dateNodes.size() == 0 && dateRangeNodes.size() == 1)) {

            if (dateNodes.size() == 1) {

                Element dateElement = (Element) dateNodes.get(0);
                String value = dateElement.getAttributeValue("value");
                String text = dateElement.getValue();

                if (value == null || value.isEmpty()) {
                    throw new IllegalArgumentException("No value attribute present in date element '" + dateElement.toXML() + "'");
                }

                dateCei = new DateExact(value, text);

            } else {

                Element dateRangeElement = (Element) dateRangeNodes.get(0);
                String from = dateRangeElement.getAttributeValue("from");
                String to = dateRangeElement.getAttributeValue("to");
                String text = dateRangeElement.getValue();

                if ((from == null || from.isEmpty()) || (to == null || to.isEmpty())) {
                    throw new MomcaException(
                            "At least either 'to' or 'from' element must be present in the 'dateRange' Element `"
                                    + dateRangeElement.toXML() + "`");
                }

                dateCei = new DateRange(from, to, text);

            }

        } else {
            throw new MomcaException("No date present in provided xml!");
        }

        return new Date(dateCei);

    }

    private Element readDiplomaticAnalysis(Element xml) {
        Nodes queryResult = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_DIPLOMATIC_ANALYSIS);
        return queryResult.size() == 0 ? new Element("cei:diplomaticAnalysis", CEI_URI) : (Element) queryResult.get(0);
    }

    private Idno readIdno(Element xml) {

        String idnoId = Util.queryXmlToString(xml, XpathQuery.QUERY_CEI_BODY_IDNO_ID);
        String idnoText = Util.queryXmlToString(xml, XpathQuery.QUERY_CEI_BODY_IDNO_TEXT);

        if (idnoId.isEmpty() || idnoText.isEmpty()) {
            throw new MomcaException("There is no idno content in the provided xml!");
        }

        String idnoOld = Util.queryXmlToString(xml, XpathQuery.QUERY_CEI_BODY_IDNO_OLD);

        Idno idno;

        if (idnoOld.isEmpty()) {
            idno = new Idno(idnoId, idnoText);
        } else {
            idno = new Idno(idnoId, idnoText, idnoOld);
        }

        return idno;

    }

    private Optional<PlaceName> readIssuedPlace(Element xml) {

        Nodes nodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_ISSUED_PLACE_NAME);

        Optional<PlaceName> place = Optional.empty();

        if (nodes.size() != 0) {

            Element issuedElement = (Element) nodes.get(0);

            StringBuilder contentBuilder = new StringBuilder();
            for (int i = 0; i < issuedElement.getChildCount(); i++) {
                contentBuilder.append(issuedElement.getChild(i).toXML());
            }
            String contentString = contentBuilder.toString();

            if (!contentString.isEmpty()) {

                String certainty = issuedElement.getAttributeValue("certainty");
                String reg = issuedElement.getAttributeValue("reg");
                String type = issuedElement.getAttributeValue("type");

                place = Optional.of(
                        new PlaceName(
                                contentString,
                                certainty == null ? "" : certainty,
                                reg == null ? "" : reg,
                                type == null ? "" : type
                        ));

            }

        }

        return place;

    }

    private Optional<String> readMixedContentElement(Element xml, XpathQuery query) {
        String queryResult = Util.queryXmlToString(xml, query);
        return queryResult.isEmpty() ? Optional.empty() : Optional.of(queryResult);
    }

    private Optional<SourceDesc> readSourceDesc(Element xml) {

        Optional<SourceDesc> result = Optional.empty();

        Nodes sourceDescNodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_SOURCE_DESC);

        if (sourceDescNodes.size() != 0) {

            Element sourceDescElement = (Element) sourceDescNodes.get(0);
            List<String> abstractBiblEntries = readBiblEntries(sourceDescElement, "sourceDescRegest");
            List<String> tenorBiblEntries = readBiblEntries(sourceDescElement, "sourceDescVolltext");

            if (!abstractBiblEntries.get(0).isEmpty() || !tenorBiblEntries.get(0).isEmpty()) {
                result = Optional.of(new SourceDesc(abstractBiblEntries, tenorBiblEntries));
            }


        }

        return result;

    }

    private List<Node> readUnusedFrontElements(Element xml) {

        List<Node> results = new ArrayList<>(0);

        Nodes nodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_FRONT);
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof Element && ((Element) node).getLocalName().equals("sourceDesc")) {
                results.add(node);
            }
        }

        return results;

    }

    public void setAbstract(@NotNull Abstract charterAbstract) {

        if (charterAbstract.getContent().isEmpty()) {
            this.charterAbstract = Optional.empty();
        } else {
            this.charterAbstract = Optional.of(charterAbstract);
        }

        updateXmlContent();

    }

    public void setCharterClass(@NotNull String charterClass) {

        if (charterClass.isEmpty()) {
            this.charterClass = Optional.empty();
        } else {
            this.charterClass = Optional.of(charterClass);
        }

        updateXmlContent();

    }

    public void setCharterStatus(@NotNull CharterStatus charterStatus) {
        this.charterStatus = charterStatus;
        setParentUri(createParentUri(getId(), charterStatus, creator.get()));
        setResourceName(createResourceName(getId(), charterStatus));
    }

    public void setDate(@NotNull Date date) {
        this.date = date;
        updateXmlContent();
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

        updateXmlContent();

    }

    public void setIdno(@NotNull Idno idno) {
        this.idno = idno;
        updateXmlContent();
    }

    public void setIssuedPlace(@NotNull PlaceName issuedPlace) {

        if (issuedPlace.getContent().isEmpty()) {
            this.issuedPlace = Optional.empty();
        } else {

            this.issuedPlace = Optional.of(issuedPlace);
        }

        updateXmlContent();

    }

    public void setLangMom(@NotNull String langMom) {

        if (langMom.isEmpty()) {
            this.langMom = Optional.empty();
        } else {
            this.langMom = Optional.of(langMom);
        }

        updateXmlContent();

    }

    public void setSourceDesc(@NotNull SourceDesc sourceDesc) {

        List<Bibl> abstractBibl = sourceDesc.getBibliographyAbstract().getEntries();
        List<Bibl> tenorBibl = sourceDesc.getBibliographyTenor().getEntries();

        if ((abstractBibl.isEmpty() || abstractBibl.get(0).getContent().isEmpty()) && (tenorBibl.isEmpty() ||
                tenorBibl.get(0).getContent().isEmpty())) {

            this.sourceDesc = Optional.empty();

        } else {

            this.sourceDesc = Optional.of(sourceDesc);

        }

        updateXmlContent();

    }

    public void setTenor(@NotNull Tenor tenor) {

        if (tenor.getContent().isEmpty()) {
            this.tenor = Optional.empty();
        } else {
            this.tenor = Optional.of(tenor);
        }

        updateXmlContent();

    }

    @NotNull
    public Element toCei() {
        Element xml = (Element) toDocument().getRootElement().copy();
        Element atomContent = xml.getFirstChildElement("content", Namespace.ATOM.getUri());
        return atomContent.getFirstChildElement("text", CEI_URI);
    }

    @Override
    void updateXmlContent() {

        Element cei = new Element("cei:text", CEI_URI);
        cei.addAttribute(new Attribute("type", "charter"));

        Element front = createFrontXml();
        Element body = createBodyXml();
        Element back = createBackXml();

        cei.appendChild(front);
        cei.appendChild(body);
        cei.appendChild(back);

        setXmlContent(new Document(new AtomEntry(id.getContentXml(), createAtomAuthor(), AtomResource.localTime(), cei)));

        try {
            validateCei(this);
        } catch (@NotNull SAXException | IOException | ParsingException | ParserConfigurationException e) {
            throw new IllegalArgumentException("Failed to validate the resource.", e);
        }

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

        Nodes ceiTextNodes = resource.queryContentAsNodes(XpathQuery.QUERY_CEI_TEXT);

        if (ceiTextNodes.size() != 1) {
            throw new IllegalArgumentException("XML content has no 'cei:text' element therefor it is probably not" +
                    " a mom-ca charter.");
        }

        Element ceiTextElement = (Element) ceiTextNodes.get(0);

        Builder builder = new Builder(reader);
        builder.build(ceiTextElement.toXML(), CEI_URI);

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
