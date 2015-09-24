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
    private List<Note> backDivNotes = new ArrayList<>(0);
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
        charterAbstract = readAbstract(xml);
        langMom = Util.queryXmlToOptional(xml, XpathQuery.QUERY_CEI_LANG_MOM);
        charterClass = Util.queryXmlToOptional(xml, XpathQuery.QUERY_CEI_CLASS);
        issuedPlace = readIssuedPlace(xml);
        backPlaceNames = readBackPlaceNames(xml);
        backPersNames = readBackPersNames(xml);
        backIndexes = readBackIndexes(xml);
        backDivNotes = readBackDivNotes(xml);

    }

    private Element createBackXml() {

        Element back = new Element("cei:back", CEI_URI);

        backPersNames.forEach(persName -> back.appendChild(persName.copy()));
        backPlaceNames.forEach(placeName -> back.appendChild(placeName.copy()));
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

        Element issued = new Element("cei:issued", CEI_URI);
        chDesc.appendChild(issued);
        issuedPlace.ifPresent(p -> issued.appendChild(p.copy()));
        issued.appendChild(date.toCeiDate());

        charterAbstract.ifPresent(a -> chDesc.appendChild(a.copy()));

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

        tenor.ifPresent(t -> body.appendChild(t.copy()));

        return body;

    }

    private Element createFrontXml() {

        Element front = new Element("cei:front", CEI_URI);

        sourceDesc.ifPresent(front::appendChild);
        unusedFrontNodes.forEach(element -> front.appendChild(element.copy()));

        return front;

    }

    private Optional<Index> createIndexInstance(Element indexElement) {

        Optional<Index> index = Optional.empty();

        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < indexElement.getChildCount(); i++) {
            contentBuilder.append(indexElement.getChild(i).toXML());
        }
        String contentString = contentBuilder.toString();

        if (!contentString.isEmpty()) {

            String indexName = indexElement.getAttributeValue("indexName");
            String lemma = indexElement.getAttributeValue("lemma");
            String sublemma = indexElement.getAttributeValue("sublemma");
            String type = indexElement.getAttributeValue("type");
            String id = indexElement.getAttributeValue("id");
            String facs = indexElement.getAttributeValue("facs");
            String lang = indexElement.getAttributeValue("lang");
            String n = indexElement.getAttributeValue("n");

            index = Optional.of(
                    new Index(
                            contentString,
                            indexName == null ? "" : indexName,
                            lemma == null ? "" : lemma,
                            sublemma == null ? "" : sublemma,
                            type == null ? "" : type,
                            id == null ? "" : id,
                            facs == null ? "" : facs,
                            lang == null ? "" : lang,
                            n == null ? "" : n
                    ));

        }

        return index;

    }

    private Optional<Note> createNoteInstance(Element noteElement) {

        Optional<Note> place = Optional.empty();

        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < noteElement.getChildCount(); i++) {
            contentBuilder.append(noteElement.getChild(i).toXML());
        }
        String contentString = contentBuilder.toString();

        if (!contentString.isEmpty()) {

            String placeAttribute = noteElement.getAttributeValue("place");
            String idAttribute = noteElement.getAttributeValue("id");
            String nAttribute = noteElement.getAttributeValue("n");

            place = Optional.of(
                    new Note(
                            contentString,
                            placeAttribute == null ? "" : placeAttribute,
                            idAttribute == null ? "" : idAttribute,
                            nAttribute == null ? "" : nAttribute
                    ));

        }

        return place;

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

    private Optional<PersName> createPersNameInstance(Element persNameElement) {

        Optional<PersName> persName = Optional.empty();

        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < persNameElement.getChildCount(); i++) {
            contentBuilder.append(persNameElement.getChild(i).toXML());
        }
        String contentString = contentBuilder.toString();

        if (!contentString.isEmpty()) {

            String certainty = persNameElement.getAttributeValue("certainty");
            String reg = persNameElement.getAttributeValue("reg");
            String type = persNameElement.getAttributeValue("type");
            String key = persNameElement.getAttributeValue("key");
            String facs = persNameElement.getAttributeValue("facs");
            String id = persNameElement.getAttributeValue("id");
            String lang = persNameElement.getAttributeValue("lang");
            String n = persNameElement.getAttributeValue("n");

            persName = Optional.of(
                    new PersName(
                            contentString,
                            certainty == null ? "" : certainty,
                            reg == null ? "" : reg,
                            type == null ? "" : type,
                            key == null ? "" : key,
                            facs == null ? "" : facs,
                            id == null ? "" : id,
                            lang == null ? "" : lang,
                            n == null ? "" : n
                    ));

        }

        return persName;

    }

    private Optional<PlaceName> createPlaceNameInstance(Element placeNameElement) {

        Optional<PlaceName> place = Optional.empty();

        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < placeNameElement.getChildCount(); i++) {
            contentBuilder.append(placeNameElement.getChild(i).toXML());
        }
        String contentString = contentBuilder.toString();

        if (!contentString.isEmpty()) {

            String certainty = placeNameElement.getAttributeValue("certainty");
            String existent = placeNameElement.getAttributeValue("existent");
            String facs = placeNameElement.getAttributeValue("facs");
            String id = placeNameElement.getAttributeValue("id");
            String key = placeNameElement.getAttributeValue("key");
            String lang = placeNameElement.getAttributeValue("lang");
            String n = placeNameElement.getAttributeValue("n");
            String reg = placeNameElement.getAttributeValue("reg");
            String type = placeNameElement.getAttributeValue("type");


            place = Optional.of(
                    new PlaceName(
                            contentString,
                            certainty == null ? "" : certainty,
                            reg == null ? "" : reg,
                            type == null ? "" : type,
                            existent == null ? "" : existent,
                            key == null ? "" : key,
                            facs == null ? "" : facs,
                            id == null ? "" : id,
                            lang == null ? "" : lang,
                            n == null ? "" : n
                    ));

        }

        return place;

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

    private Optional<Abstract> readAbstract(Element xml) {

        Optional<Abstract> result = Optional.empty();

        Nodes nodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_ABSTRACT);

        if (nodes.size() != 0) {

            Element element = (Element) nodes.get(0);

            String content = Util.joinChildNodes(element);

            if (!content.isEmpty()) {

                String facs = element.getAttributeValue("facs");
                String id = element.getAttributeValue("id");
                String lang = element.getAttributeValue("lang");
                String n = element.getAttributeValue("n");

                result = Optional.of(
                        new Abstract(
                                content,
                                facs == null ? "" : facs,
                                id == null ? "" : id,
                                lang == null ? "" : lang,
                                n == null ? "" : n
                        ));
                
            }


        }

        return result;

    }

    private List<Note> readBackDivNotes(Element xml) {

        List<Note> results = new ArrayList<>(0);

        Nodes nodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_BACK_NOTE);

        for (int i = 0; i < nodes.size(); i++) {
            Element noteElement = (Element) nodes.get(i);
            createNoteInstance(noteElement).ifPresent(results::add);
        }

        return results;

    }

    private List<Index> readBackIndexes(Element xml) {

        List<Index> results = new ArrayList<>(0);

        Nodes nodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_BACK_INDEX);

        for (int i = 0; i < nodes.size(); i++) {
            Element indexElement = (Element) nodes.get(i);
            createIndexInstance(indexElement).ifPresent(results::add);
        }

        return results;
    }

    private List<PersName> readBackPersNames(Element xml) {

        List<PersName> results = new ArrayList<>(0);

        Nodes nodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_BACK_PERS_NAME);

        for (int i = 0; i < nodes.size(); i++) {
            Element persNameElement = (Element) nodes.get(i);
            createPersNameInstance(persNameElement).ifPresent(results::add);
        }

        return results;

    }

    private List<PlaceName> readBackPlaceNames(Element xml) {

        List<PlaceName> results = new ArrayList<>(0);

        Nodes nodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_CEI_BACK_PLACE_NAME);

        for (int i = 0; i < nodes.size(); i++) {
            Element placeNameElement = (Element) nodes.get(i);
            createPlaceNameInstance(placeNameElement).ifPresent(results::add);
        }


        return results;

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

            place = createPlaceNameInstance(issuedElement);

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

    public void setBackDivNotes(@NotNull List<Note> backDivNotes) {
        this.backDivNotes = backDivNotes;
        updateXmlContent();
    }

    public void setBackIndexes(@NotNull List<Index> backIndexes) {
        this.backIndexes = backIndexes;
        updateXmlContent();
    }

    public void setBackPersNames(@NotNull List<PersName> backPersNames) {
        this.backPersNames = backPersNames;
        updateXmlContent();
    }

    public void setBackPlaceNames(@NotNull List<PlaceName> backPlaceNames) {
        this.backPlaceNames = backPlaceNames;
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
