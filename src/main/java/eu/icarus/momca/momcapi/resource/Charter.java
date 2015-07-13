package eu.icarus.momca.momcapi.resource;


import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.XmlValidationProblem;
import eu.icarus.momca.momcapi.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.xml.atom.AtomIdCharter;
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
public class Charter extends ExistResource {

    @NotNull
    private static final String CEI_SCHEMA_URL = "https://raw.githubusercontent.com/icaruseu/mom-ca/master/my/XRX/src/mom/app/cei/xsd/cei10.xsd";
    @NotNull
    private final Optional<AtomAuthor> atomAuthor;
    @NotNull
    private final AtomIdCharter atomId;
    @NotNull
    private final Optional<AbstractCeiDate> ceiDate;
    @NotNull
    private final CeiIdno ceiIdno;
    @NotNull
    private final List<CeiFigure> ceiWitnessOrigFigures;
    @NotNull
    private final CharterStatus status;
    @NotNull
    private final List<XmlValidationProblem> validationProblems = new ArrayList<>(0);


    private class SimpleErrorHandler implements ErrorHandler {

        public void error(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.Level.ERROR, e);
        }

        public void fatalError(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.Level.FATAL_ERROR, e);
        }

        public void warning(@NotNull SAXParseException e) throws SAXException {
            addToXmlValidationProblem(XmlValidationProblem.Level.WARNING, e);
        }

        private void addToXmlValidationProblem(@NotNull XmlValidationProblem.Level level, @NotNull SAXParseException e) {
            validationProblems.add(new XmlValidationProblem(level, e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
        }

    }

    /**
     * Instantiates a new Charter.
     *
     * @param existResource The eXist-resource that the charter is based on. The XML-content is validated against the <a href="https://github.com/icaruseu/mom-ca/blob/master/my/XRX/src/mom/app/cei/xsd/cei10.xsd">CEI Schema</a>.
     */
    public Charter(@NotNull ExistResource existResource) {

        super(existResource);

        try {
            validateCei(existResource);
        } catch (@NotNull SAXException | IOException | ParsingException | ParserConfigurationException e) {
            throw new IllegalArgumentException("Failed to validate the resource.", e);
        }

        status = initStatus();

        atomId = initCharterAtomId();
        atomAuthor = initAtomAuthor();
        ceiIdno = initCeiIdno();
        ceiDate = initCeiDate();
        ceiWitnessOrigFigures = new ArrayList<>(initCeiWitnessOrigFigures());

    }

    /**
     * @return The AtomAuthor.
     */
    @NotNull
    public Optional<AtomAuthor> getAtomAuthor() {
        return atomAuthor;
    }

    /**
     * @return The AtomId.
     */
    @NotNull
    public AtomIdCharter getAtomId() {
        return atomId;
    }

    /**
     * @return The date.
     */
    @NotNull
    public Optional<AbstractCeiDate> getCeiDate() {
        return ceiDate;
    }

    /**
     * @return The CeiIdno.
     */
    @NotNull
    public CeiIdno getCeiIdno() {
        return ceiIdno;
    }

    /**
     * @return A list of all figures in {@code cei:witnessOrig}. They represent only the direct images of the charter.
     */
    @NotNull
    public List<CeiFigure> getCeiWitnessOrigFigures() {
        return ceiWitnessOrigFigures;
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
                ", ceiIdno=" + ceiIdno +
                ", ceiWitnessOrigFigures=" + ceiWitnessOrigFigures +
                ", status=" + status +
                "} " + super.toString();

    }

    private Optional<AtomAuthor> initAtomAuthor() {

        Optional<AtomAuthor> atomAuthor = Optional.empty();
        String authorEmail = queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL);
        if (!authorEmail.isEmpty()) {
            atomAuthor = Optional.of(new AtomAuthor(authorEmail));
        }
        return atomAuthor;

    }

    @NotNull
    private Optional<AbstractCeiDate> initCeiDate() {

        Optional<AbstractCeiDate> ceiDateOptional = Optional.empty();

        Nodes ceiIssuedNodes = listQueryResultNodes(XpathQuery.QUERY_CEI_ISSUED);
        if (ceiIssuedNodes.size() != 0) {

            Element ceiIssued = (Element) ceiIssuedNodes.get(0);

            Elements dateElements = ceiIssued.getChildElements("date", eu.icarus.momca.momcapi.xml.Namespace.CEI.getUri());
            Elements dateRangeElements = ceiIssued.getChildElements("dateRange", eu.icarus.momca.momcapi.xml.Namespace.CEI.getUri());


            if (dateElements.size() == 1 && dateRangeElements.size() == 0) {

                Element dateElement = dateElements.get(0);
                String value = dateElement.getAttributeValue("value");
                String literalDate = dateElement.getValue();
                ceiDateOptional = Optional.of(new CeiDate(value, literalDate));

            } else if (dateElements.size() == 0 && dateRangeElements.size() == 1) {

                Element dateRangeElement = dateRangeElements.get(0);
                String from = dateRangeElement.getAttributeValue("from");
                String to = dateRangeElement.getAttributeValue("to");
                String literalDate = dateRangeElement.getValue();
                ceiDateOptional = Optional.of(new CeiDateRange(from, to, literalDate));

            }

        }

        return ceiDateOptional;
    }

    @NotNull
    private CeiIdno initCeiIdno() {
        return new CeiIdno(queryUniqueElement(XpathQuery.QUERY_CEI_BODY_IDNO_ID), queryUniqueElement(XpathQuery.QUERY_CEI_BODY_IDNO_TEXT));
    }

    @NotNull
    private List<CeiFigure> initCeiWitnessOrigFigures() {

        List<CeiFigure> results = new ArrayList<>(0);

        Nodes figures = listQueryResultNodes(XpathQuery.QUERY_CEI_WITNESS_ORIG_FIGURE);

        for (int i = 0; i < figures.size(); i++) {

            Element figure = (Element) figures.get(i);
            String n = figure.getAttribute("n") == null ? "" : figure.getAttribute("n").getValue();
            Elements childElements = figure.getChildElements("graphic", eu.icarus.momca.momcapi.xml.Namespace.CEI.getUri());

            switch (childElements.size()) {

                case 0:
                    break;

                case 1:
                    Element graphic = childElements.get(0);
                    String url = graphic.getAttribute("url") == null ? "" : childElements.get(0).getAttribute("url").getValue();
                    String text = graphic.getValue();
                    results.add(new CeiFigure(url, n, text));
                    break;

                default:
                    throw new IllegalArgumentException("More than one child-elements of 'cei:figure'. Only one allowed, 'cei:graphic'.");

            }

        }

        return results;

    }

    @NotNull
    private AtomIdCharter initCharterAtomId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);
        if (idString.isEmpty()) {
            throw new IllegalArgumentException(String.format("No atom:id in xml content: '%s'", getXmlAsDocument().toXML()));
        } else {
            return new AtomIdCharter(idString);
        }

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

    @NotNull
    private String queryUniqueElement(@NotNull XpathQuery query) {

        List<String> atomQueryResults = listQueryResultStrings(query);

        String result;

        switch (atomQueryResults.size()) {
            case 0:
                result = "";
                break;
            case 1:
                result = atomQueryResults.get(0);
                break;
            default:
                throw new IllegalArgumentException(String.format("More than one results for Query '%s'", query.getQuery()));
        }

        return result;

    }

    private void validateCei(@NotNull ExistResource resource) throws SAXException, ParserConfigurationException, ParsingException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        factory.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(CEI_SCHEMA_URL)}));

        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new SimpleErrorHandler());

        Nodes ceiTextNodes = resource.listQueryResultNodes(XpathQuery.QUERY_CEI_TEXT);

        if (ceiTextNodes.size() != 1) {
            throw new IllegalArgumentException("XML content has no 'cei:text' element therefor it is probably not a mom-ca charter.");
        }

        Element ceiText = (Element) ceiTextNodes.get(0);

        Builder builder = new Builder(reader);
        builder.build(ceiText.toXML(), Namespace.CEI.getUri());

    }

}
