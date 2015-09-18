package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.id.IdCharter;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.model.xml.xrx.Saved;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a user in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 24.06.2015.
 */
public class User extends ExistResource {

    @NotNull
    private List<Element> annotations = new ArrayList<>(0);
    @NotNull
    private List<Element> askElements = new ArrayList<>(0);
    @NotNull
    private List<IdCharter> bookmarkedCharters = new ArrayList<>(0);
    @NotNull
    private List<Element> croppings = new ArrayList<>(0);
    @NotNull
    private String firstname = "";
    @NotNull
    private IdUser id;
    @NotNull
    private IdUser idModerator;
    @NotNull
    private String info = "";
    @NotNull
    private String institution = "";
    @NotNull
    private String name = "";
    @NotNull
    private String phone = "";
    @NotNull
    private List<Saved> savedCharters = new ArrayList<>(0);
    @NotNull
    private String street = "";
    @NotNull
    private String town = "";
    @NotNull
    private String zip = "";

    public User(@NotNull String identifier, @NotNull String moderatorIdentifier) {

        super(identifier + ".xml", ResourceRoot.USER_DATA.getUri(), "<empty/>");

        if (identifier.isEmpty() || moderatorIdentifier.isEmpty()) {
            throw new IllegalArgumentException("User and moderator identifiers are not allowed to be empty strings.");
        }

        this.id = new IdUser(identifier);
        this.idModerator = new IdUser(moderatorIdentifier);

        updateXmlContent();

    }

    /**
     * Instantiates a new User.
     *
     * @param existResource The eXist Resource of the user in the database.
     */
    public User(@NotNull ExistResource existResource) {

        super(existResource);

        Element xml = this.toDocument().getRootElement();

        this.id = getChildElement(xml, "email").map(element -> new IdUser(element.getValue())).orElseThrow(RuntimeException::new);
        this.idModerator = getChildElement(xml, "moderator").map(element -> new IdUser(element.getValue())).orElseThrow(RuntimeException::new);

        this.firstname = getChildElement(xml, "firstname").map(Element::getValue).orElse("");
        this.name = getChildElement(xml, "name").map(Element::getValue).orElse("");
        this.street = getChildElement(xml, "street").map(Element::getValue).orElse("");
        this.zip = getChildElement(xml, "zip").map(Element::getValue).orElse("");
        this.town = getChildElement(xml, "town").map(Element::getValue).orElse("");
        this.phone = getChildElement(xml, "phone").map(Element::getValue).orElse("");
        this.institution = getChildElement(xml, "institution").map(Element::getValue).orElse("");
        this.info = getChildElement(xml, "info").map(Element::getValue).orElse("");

        this.askElements = getListElements(xml, "askelements");
        this.croppings = getListElements(xml, "cropping");
        this.annotations = getListElements(xml, "annotations");


    }

    @NotNull
    private Element createElement(@NotNull String localizedName, @NotNull String content) {

        Element element = new Element("xrx:" + localizedName, Namespace.XRX.getUri());

        if (!content.isEmpty()) {
            element.appendChild(content);
        }

        return element;

    }

    @NotNull
    public List<Element> getAnnotations() {
        return annotations;
    }

    @NotNull
    public List<Element> getAskElements() {
        return askElements;
    }

    @NotNull
    public List<IdCharter> getBookmarkedCharters() {
        return bookmarkedCharters;
    }

    private Optional<Element> getChildElement(@NotNull Element xml, @NotNull String localName) {

        Optional<Element> result = Optional.empty();

        Element child = xml.getFirstChildElement(localName, Namespace.XRX.getUri());
        if (child != null) {
            result = Optional.of(child);
        }

        return result;

    }

    @NotNull
    public List<Element> getCroppings() {
        return croppings;
    }

    @NotNull
    public String getFirstname() {
        return firstname;
    }

    @NotNull
    public IdUser getId() {
        return id;
    }

    @NotNull
    public IdUser getIdModerator() {
        return idModerator;
    }

    /**
     * @return The name of the user.
     */
    @NotNull
    public String getIdentifier() {
        return id.getIdentifier();
    }

    @NotNull
    public String getInfo() {
        return info;
    }

    @NotNull
    public String getInstitution() {
        return institution;
    }

    private List<Element> getListElements(Element xml, String localName) {

        List<Element> elements = new ArrayList<>(0);

        Optional<Element> element = getChildElement(xml, localName);

        if (element.isPresent()) {

            Elements childElements = element.get().getChildElements();

            for (int i = 0; i < childElements.size(); i++) {
                elements.add(childElements.get(i));
            }

        }

        return elements;

    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getPhone() {
        return phone;
    }

    @NotNull
    public List<Saved> getSavedCharters() {
        return savedCharters;
    }

    @NotNull
    public String getStreet() {
        return street;
    }

    @NotNull
    public String getTown() {
        return town;
    }

    @NotNull
    public String getZip() {
        return zip;
    }

    /**
     * @return A list of the charters the user has bookmarked.
     */
    @NotNull
    public List<IdCharter> listBookmarkedCharterIds() {
        return queryContentAsList(XpathQuery.QUERY_XRX_BOOKMARK).stream()
                .map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    /**
     * @return A list of the charters the user has saved.
     */
    @NotNull
    public List<IdCharter> listSavedCharterIds() {
        return queryContentAsList(XpathQuery.QUERY_XRX_SAVED_ID).stream()
                .map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    private String queryUniqueFieldValue(@NotNull XpathQuery query) {

        List<String> queryResults = queryContentAsList(query);

        if (queryResults.size() == 1) {
            return queryResults.get(0);
        } else {
            throw new IllegalArgumentException("The XML content of the resource doesn't have an 'xrx:name' element. " +
                    "It's probably not a valid user resource.");
        }

    }


    public void setAnnotations(@NotNull List<Element> annotations) {
        this.annotations = annotations;
        updateXmlContent();
    }

    public void setAskElements(@NotNull List<Element> askElements) {
        this.askElements = askElements;
        updateXmlContent();
    }

    public void setBookmarkedCharters(@NotNull List<IdCharter> bookmarkedCharters) {
        this.bookmarkedCharters = bookmarkedCharters;
        updateXmlContent();
    }

    public void setCroppings(@NotNull List<Element> cropping) {
        this.croppings = cropping;
        updateXmlContent();
    }

    public void setFirstname(@NotNull String firstname) {
        this.firstname = firstname;
        updateXmlContent();
    }

    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string!");
        }

        this.id = new IdUser(identifier);

        updateXmlContent();

    }

    public void setInfo(@NotNull String info) {
        this.info = info;
        updateXmlContent();
    }

    public void setInstitution(@NotNull String institution) {
        this.institution = institution;
        updateXmlContent();
    }

    public void setModerator(@NotNull String moderatorIdentifier) {

        if (moderatorIdentifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string!");
        }

        this.idModerator = new IdUser(moderatorIdentifier);

        updateXmlContent();

    }

    public void setName(@NotNull String name) {
        this.name = name;
        updateXmlContent();
    }

    public void setPhone(@NotNull String phone) {
        this.phone = phone;
        updateXmlContent();
    }

    public void setSavedCharters(@NotNull List<Saved> savedCharters) {
        this.savedCharters = savedCharters;
        updateXmlContent();
    }

    public void setStreet(@NotNull String street) {
        this.street = street;
        updateXmlContent();
    }

    public void setTown(@NotNull String town) {
        this.town = town;
        updateXmlContent();
    }

    public void setZip(@NotNull String zip) {
        this.zip = zip;
        updateXmlContent();
    }

    @NotNull
    public AtomAuthor toAtomAuthor() {
        return new AtomAuthor(id.getIdentifier());
    }

    private void updateXmlContent() {

        String uri = Namespace.XRX.getUri();

        Element root = new Element("xrx:user", uri);

        root.appendChild(createElement("username", ""));
        root.appendChild(createElement("password", ""));
        root.appendChild(createElement("firstname", firstname));
        root.appendChild(createElement("name", name));
        root.appendChild(createElement("email", getIdentifier()));
        root.appendChild(createElement("moderator", getIdModerator().getIdentifier()));
        root.appendChild(createElement("street", street));
        root.appendChild(createElement("zip", zip));
        root.appendChild(createElement("town", town));
        root.appendChild(createElement("phone", phone));
        root.appendChild(createElement("institution", institution));
        root.appendChild(createElement("info", info));

        if (this.askElements.size() != 0) {
            Element askElements = new Element("xrx:askelements", uri);
            this.askElements.forEach(askElements::appendChild);
            root.appendChild(askElements);
        }

        if (this.croppings.size() != 0) {
            Element cropping = new Element("xrx:croppings", uri);
            this.croppings.forEach(cropping::appendChild);
            root.appendChild(cropping);
        }

        if (this.annotations.size() != 0) {
            Element annotations = new Element("xrx:annotations", uri);
            this.annotations.forEach(annotations::appendChild);
            root.appendChild(annotations);
        }

        Element storage = new Element("xrx:storage", uri);
        Element savedList = new Element("xrx:saved_list", uri);
        savedCharters.forEach(savedList::appendChild);
        storage.appendChild(savedList);

        Element bookmarkList = new Element("xrx:bookmark_list", uri);
        bookmarkedCharters.forEach(idCharter -> bookmarkList.appendChild(createElement("bookmark", idCharter.getContentXml().getText())));
        storage.appendChild(bookmarkList);

        root.appendChild(storage);

        setXmlContent(new Document(root));

    }


}
