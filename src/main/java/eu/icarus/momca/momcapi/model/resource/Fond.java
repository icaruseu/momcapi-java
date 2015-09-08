package eu.icarus.momca.momcapi.model.resource;

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

    public Fond(@NotNull ExistResource fondResource, @NotNull Optional<ExistResource> fondPreferences) {

        super(fondResource);

        id = initId();
        this.fondPreferences = fondPreferences;
        this.name = queryUniqueElement(XpathQuery.QUERY_EAD_UNITTITLE);
        this.imageAccess = initImageAccess();
        this.dummyImageUrl = initDummyImageUrl();
        this.imagesUrl = initImagesUrl();

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

    @NotNull
    private Optional<URL> initDummyImageUrl() {

        Optional<URL> url = Optional.empty();

        if (fondPreferences.isPresent()) {
            String urlString = fondPreferences.get().queryUniqueElement(XpathQuery.QUERY_XRX_DUMMY_IMAGE_URL);
            url = createUrl(urlString);
        }

        return url;

    }

    @NotNull
    private IdFond initId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", toDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdFond(new AtomId(idString));
        }

    }

    @NotNull
    private Optional<ImageAccess> initImageAccess() {

        Optional<ImageAccess> access = Optional.empty();

        if (fondPreferences.isPresent()) {

            String imageAccessString = fondPreferences.get().queryUniqueElement(XpathQuery.QUERY_XRX_IMAGE_ACCESS);
            access = Optional.of(ImageAccess.fromText(imageAccessString));

        }

        return access;
    }

    @NotNull
    private Optional<URL> initImagesUrl() {

        Optional<URL> url = Optional.empty();

        if (fondPreferences.isPresent()) {
            String urlString = fondPreferences.get().queryUniqueElement(XpathQuery.QUERY_XRX_IMAGE_SERVER_BASE_URL);
            url = createUrl(urlString);
        }

        return url;

    }

    private void resetOddList() {
        oddList.clear();
        oddList.add(new Odd());
    }

    public void setArchiveId(@NotNull IdArchive idArchive) {
        this.id = new IdFond(idArchive.getIdentifier(), getId().getIdentifier());
        updateParentUri();
        updateXmlContent();
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

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.id = new IdFond(getArchiveId().getIdentifier(), identifier);

        setResourceName(identifier + ResourceType.FOND.getNameSuffix());
        updateParentUri();

        updateXmlContent();

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
