package eu.icarus.momca.momcapi.model.resource;


import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.XPathContext;
import org.jetbrains.annotations.NotNull;

/**
 * An XML resource stored in the database used by MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 24.06.2015.
 */
public class ExistResource {

    @NotNull
    private String parentUri;
    @NotNull
    private String resourceName;
    @NotNull
    private Document xmlContent;


    ExistResource(@NotNull ExistResource other) {
        this.parentUri = other.parentUri;
        this.resourceName = other.resourceName;
        this.xmlContent = other.xmlContent;
    }

    /**
     * Instantiates a new ExistResource.
     *
     * @param resourceName        The name of the resource, e.g. {@code user.xmlContent}.
     * @param parentCollectionUri The URI of the collection, the resource is stored in in the database,
     *                            e.g. {@code /db/mom-data/xrx.user}.
     * @param xmlContent          The xmlContent content of the resource as {@code String}.
     */
    public ExistResource(@NotNull String resourceName, @NotNull String parentCollectionUri, @NotNull String xmlContent) {

        if (resourceName.isEmpty() || parentCollectionUri.isEmpty() || xmlContent.isEmpty()) {
            throw new IllegalArgumentException("Constructor strings are not allowed to be empty.");
        }

        this.resourceName = Util.encode(resourceName);
        this.parentUri = Util.encode(parentCollectionUri);
        this.xmlContent = Util.parseToDocument(xmlContent);

    }

    /**
     * Instantiates a new ExistResource with empty xml content.
     *
     * @param resourceName        The name of the resource, e.g. {@code user.xmlContent}.
     * @param parentCollectionUri The URI of the collection, the resource is stored in in the database,
     *                            e.g. {@code /db/mom-data/xrx.user}.
     */
    public ExistResource(@NotNull String resourceName, @NotNull String parentCollectionUri) {
        this(resourceName, parentCollectionUri, "<empty/>");
    }

    /**
     * @return The URI of the collection, the resource is stored in in the database, e.g. {@code /db/mom-data/xrx.user}.
     */
    @NotNull
    public String getParentUri() {
        return parentUri;
    }

    /**
     * @return The name of the resource in the database, e.g. {@code user.xmlContent}
     */
    @NotNull
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @return The URI of the resource in the database, e.g. {@code /db/mom-data/xrx.user/user.xmlContent}.
     */
    @NotNull
    public String getUri() {
        return parentUri + "/" + resourceName;
    }

    @NotNull
    private XPathContext getxPathContext(@NotNull Element root, @NotNull XpathQuery query) {
        XPathContext context = XPathContext.makeNamespaceContext(root);
        query.getNamespaces().forEach(n -> context.addNamespace(n.getPrefix(), n.getUri()));
        return context;
    }

    void setParentUri(@NotNull String parentUri) {

        if (parentUri.isEmpty()) {
            throw new IllegalArgumentException("The parent URI is not allowed to be an empty string.");
        }
        this.parentUri = Util.encode(parentUri);

    }

    void setResourceName(@NotNull String resourceName) {

        if (resourceName.isEmpty()) {
            throw new IllegalArgumentException("The resource name is not allowed to be an empty string.");
        }
        this.resourceName = Util.encode(resourceName);

    }

    void setXmlContent(@NotNull String xmlContent) {
        this.xmlContent = Util.parseToDocument(xmlContent);
    }

    void setXmlContent(@NotNull Document xmlContent) {
        this.xmlContent = xmlContent;
    }

    /**
     * @return The XML as an XML document.
     */
    @NotNull
    public Document toDocument() {
        return xmlContent;
    }

    @NotNull
    @Override
    public String toString() {
        return "ExistResource{" +
                "resourceName='" + resourceName + '\'' +
                ", xmlContent=" + xmlContent +
                ", parentUri='" + parentUri + '\'' +
                '}';
    }

    @NotNull
    public String toXML() {
        return xmlContent.toXML();
    }

}
