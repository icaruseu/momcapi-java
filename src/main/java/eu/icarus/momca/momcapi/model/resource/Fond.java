package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.ImageAccess;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.model.xml.ead.*;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by daniel on 17.07.2015.
 */
public class Fond extends AtomResource {

    @NotNull
    private Bibliography bibliography = new Bibliography();
    @NotNull
    private BiogHist biogHist = new BiogHist();
    @NotNull
    private CustodHist custodHist = new CustodHist();
    @NotNull
    private Optional<URL> dummyImageUrl = Optional.empty();
    @NotNull
    private Optional<ExistResource> fondPreferences = Optional.empty();
    @NotNull
    private Optional<ImageAccess> imageAccess = Optional.empty();
    @NotNull
    private Optional<URL> imagesUrl = Optional.empty();
    @NotNull
    private String name;
    @NotNull
    private List<Odd> oddList = new ArrayList<>(0);

    public Fond(@NotNull String identifier, @NotNull IdArchive parentArchive, @NotNull String name) {

        super(new IdFond(parentArchive.getIdentifier(), identifier), ResourceType.FOND, ResourceRoot.ARCHIVAL_FONDS);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;
        resetOddList();

        updateXmlContent();

    }

    public Fond(@NotNull IdFond idFond, @NotNull String fondXmlContent, @Nullable String preferencesXmlContent) {

        this(
                new ExistResource(
                        String.format("%s%s", idFond.getIdentifier(), ResourceType.FOND.getNameSuffix()),
                        String.format("%s/%s/%s", ResourceRoot.ARCHIVAL_FONDS.getUri(), idFond.getIdArchive().getIdentifier(), idFond.getIdentifier()),
                        fondXmlContent),
                preferencesXmlContent == null ?
                        Optional.empty() :
                        Optional.of(new ExistResource(
                                String.format("%s%s", idFond.getIdentifier(), ".preferences.xml"),
                                String.format("%s/%s/%s", ResourceRoot.ARCHIVAL_FONDS.getUri(), idFond.getIdArchive().getIdentifier(), idFond.getIdentifier()),
                                preferencesXmlContent
                        )));

    }

    public Fond(@NotNull ExistResource fondResource, @NotNull Optional<ExistResource> fondPreferencesResource) {

        super(fondResource);

        Element fondXml = toDocument().getRootElement();

        this.id = readId(fondXml);
        this.name = readName(fondXml);

        this.oddList = readOddList(fondXml);

        fondPreferencesResource.ifPresent(this::initFondPreferences);

    }

    @NotNull
    private Element createEadElement() {

        String uri = Namespace.EAD.getUri();


        Element ead = new Element("ead:ead", uri);
        ead.appendChild(new EadHeader());

        Element archdesc = new Element("ead:archdesc", uri);

        archdesc.addAttribute(new Attribute("level", "otherlevel"));
        Element didEmpty = new Element("ead:did", uri);
        didEmpty.appendChild(new Element("ead:abstract", uri));

        archdesc.appendChild(didEmpty);

        Element dsc = new Element("ead:dsc", uri);

        Element c = new Element("ead:c", uri);
        c.addAttribute(new Attribute("level", "fonds"));
        c.appendChild(new Did(id.getIdentifier(), name));
        c.appendChild(biogHist.copy());
        c.appendChild(custodHist.copy());
        c.appendChild(bibliography.copy());
        oddList.forEach(odd -> c.appendChild(odd.copy()));

        dsc.appendChild(c);

        archdesc.appendChild(dsc);

        ead.appendChild(archdesc);

        return ead;

    }

    @NotNull
    private Optional<URL> createUrl(@NotNull String urlString) {

        Optional<URL> url = Optional.empty();

        try {

            if (!urlString.isEmpty()) {
                url = Optional.of(new URL(urlString));
            }

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(urlString + " is not a valid URL.");
        }

        return url;

    }

    @NotNull
    public IdArchive getArchiveId() {
        return getId().getIdArchive();
    }

    @NotNull
    public Bibliography getBibliography() {
        return bibliography;
    }

    @NotNull
    public BiogHist getBiogHist() {
        return biogHist;
    }

    @NotNull
    public CustodHist getCustodHist() {
        return custodHist;
    }

    @NotNull
    public Optional<URL> getDummyImageUrl() {
        return dummyImageUrl;
    }

    @NotNull
    public Optional<ExistResource> getFondPreferences() {
        return fondPreferences;
    }

    @NotNull
    public IdFond getId() {
        return (IdFond) id;
    }

    @NotNull
    public Optional<ImageAccess> getImageAccess() {
        return imageAccess;
    }

    @NotNull
    public Optional<URL> getImagesUrl() {
        return imagesUrl;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public List<Odd> getOddList() {
        return oddList;
    }

    private void initFondPreferences(ExistResource existResource) {
        Element fondPreferencesElement = existResource.toDocument().getRootElement();

        this.imageAccess = readImageAccess(fondPreferencesElement);
        this.dummyImageUrl = readDummyImageUrl(fondPreferencesElement);
        this.imagesUrl = readImagesUrl(fondPreferencesElement);
    }

    private Optional<URL> readDummyImageUrl(@NotNull Element xml) {
        String urlString = Util.queryXmlToString(xml, XpathQuery.QUERY_XRX_DUMMY_IMAGE_URL);
        return createUrl(urlString);
    }

    @NotNull
    private IdFond readId(@NotNull Element xml) {

        String idString = Util.queryXmlToString(xml, XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", toDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdFond(new AtomId(idString));
        }

    }

    private Optional<ImageAccess> readImageAccess(Element xml) {
        String imageAccessString = Util.queryXmlToString(xml, XpathQuery.QUERY_XRX_IMAGE_ACCESS);
        return Optional.of(ImageAccess.fromText(imageAccessString));
    }

    private Optional<URL> readImagesUrl(@NotNull Element xml) {
        String urlString = Util.queryXmlToString(xml, XpathQuery.QUERY_XRX_IMAGE_SERVER_BASE_URL);
        return createUrl(urlString);
    }

    @NotNull
    private String readName(Element xml) {
        return Util.queryXmlToString(xml, XpathQuery.QUERY_EAD_UNITTITLE);
    }

    @NotNull
    private List<Odd> readOddList(@NotNull Element xml) {

        List<Odd> results = new ArrayList<>();

        // TODO
        return results;

    }

    private void resetOddList() {
        oddList.clear();
        oddList.add(new Odd());
    }

    public void setArchiveId(@NotNull IdArchive idArchive) {

        this.id = new IdFond(idArchive.getIdentifier(), getId().getIdentifier());

        updateParentUri();
        updateXmlContent();
        updatePreferencesResource();

    }

    public void setBibliography(@Nullable Bibliography bibliography) {

        if (bibliography == null) {
            this.bibliography = new Bibliography();
        } else {
            this.bibliography = bibliography;
        }

        updateXmlContent();

    }

    public void setBiogHist(@Nullable BiogHist biogHist) {

        if (biogHist == null) {
            this.biogHist = new BiogHist();
        } else {
            this.biogHist = biogHist;
        }

        updateXmlContent();

    }

    public void setCustodHist(@Nullable CustodHist custodHist) {

        if (custodHist == null) {
            this.custodHist = new CustodHist();
        } else {
            this.custodHist = custodHist;
        }

        updateXmlContent();

    }

    public void setDummyImageUrl(@Nullable URL dummyImageUrl) {

        if (dummyImageUrl == null) {
            this.dummyImageUrl = Optional.empty();
        } else {
            this.dummyImageUrl = Optional.of(dummyImageUrl);
        }

        updatePreferencesResource();

    }

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.id = new IdFond(getArchiveId().getIdentifier(), identifier);

        setResourceName(identifier + ResourceType.FOND.getNameSuffix());
        updateParentUri();

        updateXmlContent();
        updatePreferencesResource();

    }

    public void setImageAccess(@Nullable ImageAccess imageAccess) {

        if (imageAccess == null) {
            this.imageAccess = Optional.empty();
        } else {
            this.imageAccess = Optional.of(imageAccess);
        }

        updatePreferencesResource();

    }

    public void setImagesUrl(@Nullable URL imagesUrl) {

        if (imagesUrl == null) {
            this.imagesUrl = Optional.empty();
        } else {
            this.imagesUrl = Optional.of(imagesUrl);
        }

        updatePreferencesResource();

    }

    public final void setName(@NotNull String name) {

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

        updateXmlContent();

    }

    public void setOddList(@Nullable List<Odd> oddList) {

        if (oddList == null || oddList.isEmpty()) {
            resetOddList();
        } else {
            this.oddList.clear();
            this.oddList.addAll(oddList);
        }

        updateXmlContent();

    }

    @NotNull
    @Override
    public String toString() {
        return "Fond{" +
                "id=" + id +
                ", dummyImageUrl=" + dummyImageUrl +
                ", fondPreferences=" + fondPreferences +
                ", imageAccess=" + imageAccess +
                ", imagesUrl=" + imagesUrl +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }

    private void updateParentUri() {
        setParentUri(String.format("%s/%s/%s", ResourceRoot.ARCHIVAL_FONDS.getUri(),
                getId().getIdArchive().getIdentifier(), getIdentifier()));
    }

    private void updatePreferencesResource() {

        if (this.imageAccess.isPresent() || this.imagesUrl.isPresent() || this.dummyImageUrl.isPresent()) {

            String namespaceUri = Namespace.XRX.getUri();

            String preferencesName = getIdentifier() + ".preferences.xml";
            String preferencesUri = getParentUri();

            Element preferences = new Element("xrx:preferences", namespaceUri);

            Element imageAccess = new Element("xrx:param", namespaceUri);
            imageAccess.addAttribute(new Attribute("name", "image-access"));
            imageAccess.appendChild(this.imageAccess.map(ImageAccess::getText).orElse(ImageAccess.FREE.getText()));
            preferences.appendChild(imageAccess);

            Element dummyImageUrl = new Element("xrx:param", namespaceUri);
            dummyImageUrl.addAttribute(new Attribute("name", "dummy-image-url"));
            this.dummyImageUrl.ifPresent(url -> dummyImageUrl.appendChild(url.toExternalForm()));
            preferences.appendChild(dummyImageUrl);

            Element imageServerBaseUrl = new Element("xrx:param", namespaceUri);
            imageServerBaseUrl.addAttribute(new Attribute("name", "image-server-base-url"));
            this.imagesUrl.ifPresent(url -> imageServerBaseUrl.appendChild(url.toExternalForm()));
            preferences.appendChild(imageServerBaseUrl);

            this.fondPreferences = Optional.of(new ExistResource(preferencesName, preferencesUri, preferences.toXML()));

        } else {

            this.fondPreferences = Optional.empty();

        }

    }

    @Override
    void updateXmlContent() {

        Element ead = createEadElement();
        List<String> validationErrors = new ArrayList<>(0);

        try {
            validationErrors.addAll(validateEad(ead));
        } catch (SAXException | ParserConfigurationException | IOException | ParsingException e) {
            throw new RuntimeException("Failed to validate EAD.", e);
        }

        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("EAD is not valid. The following errors were reported:\n" + validationErrors.toString());
        }

        AtomId id = new AtomId(getId().getContentXml().getText());
        setXmlContent(new Document(new AtomEntry(id, createAtomAuthor(), AtomResource.localTime(), ead)));

    }

    private List<String> validateEad(@NotNull Element ead) throws SAXException, ParserConfigurationException, ParsingException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        factory.setSchema(schemaFactory.newSchema(new Source[]{
                new StreamSource(this.getClass().getResourceAsStream("/ead.xsd"))}));

        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new SimpleErrorHandler());

        Builder builder = new Builder(reader);
        builder.build(ead.toXML(), Namespace.EAD.getUri());

        return ((SimpleErrorHandler) reader.getErrorHandler()).getErrorMessages();

    }

    private class SimpleErrorHandler implements ErrorHandler {

        private List<String> errorMessages = new ArrayList<>(0);

        @Override
        public void error(SAXParseException exception) throws SAXException {
            errorMessages.add(exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            errorMessages.add(exception.getMessage());
        }

        public List<String> getErrorMessages() {
            return errorMessages;
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            errorMessages.add(exception.getMessage());
        }

    }

}
