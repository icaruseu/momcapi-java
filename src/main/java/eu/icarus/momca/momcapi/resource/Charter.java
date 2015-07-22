package eu.icarus.momca.momcapi.resource;


import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.XmlValidationProblem;
import eu.icarus.momca.momcapi.xml.atom.Author;
import eu.icarus.momca.momcapi.xml.atom.IdCharter;
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
    private final Optional<Author> atomAuthor;
    @NotNull
    private final IdCharter atomId;
    @NotNull
    private final Optional<DateAbstract> ceiDate;
    @NotNull
    private final List<Figure> ceiWitnessOrigFigures;
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

        atomId = initAtomId();
        atomAuthor = initAtomAuthor();
        idno = initCeiIdno();
        ceiDate = initCeiDate();
        ceiWitnessOrigFigures = new ArrayList<>(initCeiWitnessOrigFigures());

    }

    /**
     * @return The Author.
     */
    @NotNull
    public Optional<Author> getAtomAuthor() {
        return atomAuthor;
    }

    /**
     * @return The Id.
     */
    @NotNull
    public IdCharter getAtomId() {
        return atomId;
    }

    /**
     * @return The date.
     */
    @NotNull
    public Optional<DateAbstract> getCeiDate() {
        return ceiDate;
    }

    /**
     * @return A list of all figures in {@code cei:witnessOrig}. They represent only the direct images of the charter.
     */
    @NotNull
    public List<Figure> getCeiWitnessOrigFigures() {
        return ceiWitnessOrigFigures;
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
                "atomAuthor=" + atomAuthor +
                ", atomId=" + atomId +
                ", idno=" + idno +
                ", ceiWitnessOrigFigures=" + ceiWitnessOrigFigures +
                ", status=" + status +
                "} " + super.toString();

    }

    private Optional<Author> initAtomAuthor() {

        Optional<Author> atomAuthor = Optional.empty();
        String authorEmail = queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL);
        if (!authorEmail.isEmpty()) {
            atomAuthor = Optional.of(new Author(authorEmail));
        }
        return atomAuthor;

    }

    @NotNull
    private IdCharter initAtomId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", getXmlAsDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdCharter(idString);
        }

    }

    @NotNull
    private Optional<DateAbstract> initCeiDate() {

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
    private Idno initCeiIdno() {
        String id = queryUniqueElement(XpathQuery.QUERY_CEI_BODY_IDNO_ID);
        String text = queryUniqueElement(XpathQuery.QUERY_CEI_BODY_IDNO_TEXT);
        return new Idno(id, text);
    }

    @NotNull
    private List<Figure> initCeiWitnessOrigFigures() {

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

    private CharterStatus initStatus() {

        CharterStatus status;

        if (getParentUri().contains(ResourceRoot.METADATA_CHARTER_IMPORT.getCollectionName())) {
            status = CharterStatus.IMPORTED;
        } else if (getParentUri().contains(ResourceRoot.XRX_USER.getCollectionName())) {
            status = CharterStatus.PRIVATE;
        } else if (getParentUri().contains(ResourceRoot.METADATA_CHARTER_SAVED.getCollectionName())) {
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
