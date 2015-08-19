package eu.icarus.momca.momcapi.model;


import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.XmlValidationProblem;
import eu.icarus.momca.momcapi.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import eu.icarus.momca.momcapi.xml.cei.*;
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
 * Represents a charter in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 25.06.2015.
 */
public class Charter extends MomcaResource {

    @NotNull
    private static final String CEI_SCHEMA_URL =
            "https://raw.githubusercontent.com/icaruseu/mom-ca/master/my/XRX/src/mom/app/cei/xsd/cei10.xsd";
    @NotNull
    private final Optional<AtomAuthor> author;
    @NotNull
    private final Optional<DateAbstract> date;
    @NotNull
    private final List<Figure> figures;
    @NotNull
    private final IdCharter id;
    @NotNull
    private final Idno idno;
    @NotNull
    private final CharterStatus status;
    @NotNull
    private final List<XmlValidationProblem> validationProblems = new ArrayList<>(0);


    private class SimpleErrorHandler implements ErrorHandler {

        public void error(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.SeverityLevel.ERROR, e);
        }

        public void fatalError(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.SeverityLevel.FATAL_ERROR, e);
        }

        public void warning(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.SeverityLevel.WARNING, e);
        }

        private void addToXmlValidationProblem(@NotNull XmlValidationProblem.SeverityLevel severityLevel, @NotNull SAXParseException e) {
            validationProblems.add(new XmlValidationProblem(severityLevel, e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
        }

    }

    /**
     * Instantiates a new Charter.
     *
     * @param momcaResource The eXist-resource that the charter is based on. The XML-content is validated against the <a href="https://github.com/icaruseu/mom-ca/blob/master/my/XRX/src/mom/app/cei/xsd/cei10.xsd">CEI Schema</a>.
     */
    public Charter(@NotNull MomcaResource momcaResource) {

        super(momcaResource);

        try {
            validateCei(momcaResource);
        } catch (@NotNull SAXException | IOException | ParsingException | ParserConfigurationException e) {
            throw new IllegalArgumentException("Failed to validate the resource.", e);
        }

        status = initStatus();

        id = initId();
        author = initAuthor();
        idno = initIdno();
        date = initDate();
        figures = new ArrayList<>(initFigures());

    }

    /**
     * @return The Author.
     */
    @NotNull
    public Optional<String> getAuthorName() {
        return author.map(AtomAuthor::getEmail);
    }

    /**
     * @return The date.
     */
    @NotNull
    public Optional<DateAbstract> getDate() {
        return date;
    }

    /**
     * @return A list of all figures in the charter.
     */
    @NotNull
    public List<Figure> getFigures() {
        return figures;
    }

    /**
     * @return The Id.
     */
    @NotNull
    public IdCharter getId() {
        return id;
    }

    /**
     * @return The Idno.
     */
    @NotNull
    public Idno getIdno() {
        return idno;
    }

    /**
     * @return The charter's internal status (published, saved, etc.).
     */
    @NotNull
    public CharterStatus getStatus() {
        return status;
    }

    /**
     * @return A list of all validation problems.
     */
    @NotNull
    public List<XmlValidationProblem> getValidationProblems() {
        return validationProblems;
    }

    /**
     * @return {@code True} if there are any validation problems.
     * @see #getValidationProblems
     */
    public boolean isValidCei() {
        return validationProblems.isEmpty();
    }

    @NotNull
    @Override
    public String toString() {

        return "Charter{" +
                "author=" + author +
                ", id=" + id +
                ", idno=" + idno +
                ", figures=" + figures +
                ", status=" + status +
                "} " + super.toString();

    }

    private Optional<AtomAuthor> initAuthor() {

        Optional<AtomAuthor> author = Optional.empty();
        String authorEmail = queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL);
        if (!authorEmail.isEmpty()) {
            author = Optional.of(new AtomAuthor(authorEmail));
        }
        return author;

    }

    @NotNull
    private Optional<DateAbstract> initDate() {

        Optional<DateAbstract> ceiDateOptional = Optional.empty();
        Nodes ceiIssuedNodes = queryContentAsNodes(XpathQuery.QUERY_CEI_ISSUED);

        if (ceiIssuedNodes.size() != 0) {

            Element ceiIssued = (Element) ceiIssuedNodes.get(0);
            Elements dateElements = ceiIssued.getChildElements("date", Namespace.CEI.getUri());
            Elements dateRangeElements = ceiIssued.getChildElements("dateRange", Namespace.CEI.getUri());

            if (dateElements.size() == 1 && dateRangeElements.size() == 0) {

                Element dateElement = dateElements.get(0);
                String value = dateElement.getAttributeValue("value");
                String literalDate = dateElement.getValue();
                ceiDateOptional = Optional.of(new Date(value, literalDate));

            } else if (dateElements.size() == 0 && dateRangeElements.size() == 1) {

                Element dateRangeElement = dateRangeElements.get(0);
                String from = dateRangeElement.getAttributeValue("from");
                String to = dateRangeElement.getAttributeValue("to");
                String literalDate = dateRangeElement.getValue();
                ceiDateOptional = Optional.of(new DateRange(from, to, literalDate));

            } else if (dateElements.size() == 1 && dateRangeElements.size() == 1) {

                throw new MomcaException("Both 'cei:date' and 'cei:dateRange' present in charter XML content.");

            }

        }

        return ceiDateOptional;

    }

    @NotNull
    private List<Figure> initFigures() {

        List<Figure> figures = new ArrayList<>(0);
        Nodes figureNodes = queryContentAsNodes(XpathQuery.QUERY_CEI_WITNESS_ORIG_FIGURE);

        for (int i = 0; i < figureNodes.size(); i++) {

            Element figureElement = (Element) figureNodes.get(i);
            String nAttribute = figureElement.getAttribute("n") == null ? "" : figureElement.getAttribute("n").getValue();
            Elements childElements = figureElement.getChildElements("graphic", Namespace.CEI.getUri());

            switch (childElements.size()) {

                case 0:
                    break;

                case 1:
                    Element graphicElement = childElements.get(0);
                    String urlAttribute = (graphicElement.getAttribute("url") == null)
                            ? "" : childElements.get(0).getAttribute("url").getValue();
                    String textContent = graphicElement.getValue();
                    figures.add(new Figure(urlAttribute, nAttribute, textContent));
                    break;

                default:
                    throw new IllegalArgumentException(
                            "More than one child-elements of 'cei:figure'. Only one allowed, 'cei:graphic'.");

            }

        }

        return figures;

    }

    @NotNull
    private IdCharter initId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", getXmlAsDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdCharter(new AtomId(idString));
        }

    }

    @NotNull
    private Idno initIdno() {
        String id = queryUniqueElement(XpathQuery.QUERY_CEI_BODY_IDNO_ID);
        String text = queryUniqueElement(XpathQuery.QUERY_CEI_BODY_IDNO_TEXT);
        return new Idno(id, text);
    }

    private CharterStatus initStatus() {

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

    private void validateCei(@NotNull MomcaResource resource)
            throws SAXException, ParserConfigurationException, ParsingException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        factory.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(CEI_SCHEMA_URL)}));

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
        builder.build(ceiTextElement.toXML(), Namespace.CEI.getUri());

    }

}
