package eu.icarus.momca.momcapi.model.resource;


import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

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

    /**
     * Instantiates a new ExistResource with an existing resource.
     *
     * @param existResource The ExistResource to use.
     */
    ExistResource(@NotNull ExistResource existResource) {
        this.resourceName = existResource.getResourceName();
        this.xmlContent = existResource.toDocument();
        this.parentUri = existResource.getParentUri();
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

    /**
     * The Xpath Query to execute on the content.
     *
     * @param query the query
     * @return A list of the results as strings.
     */
    @NotNull
    @Deprecated
    final List<String> queryContentAsList(@NotNull XpathQuery query) {

        Nodes nodes = queryContentAsNodes(query);
        List<String> results = new LinkedList<>();

        for (int i = 0; i < nodes.size(); i++) {
            results.add(nodes.get(i).getValue());
        }

        return results;

    }

    /**
     * Query the resource's XML content.
     *
     * @param query The Xpath Query to execute on the content.
     * @return The nodes containing the results.
     */
    @NotNull
    @Deprecated
    final Nodes queryContentAsNodes(@NotNull XpathQuery query) {

        Element rootElement = xmlContent.getRootElement();
        String queryString = query.asString();
        XPathContext context = getxPathContext(rootElement, query);

        return rootElement.query(queryString, context);

    }

    @NotNull
    @Deprecated
    String queryUniqueElement(@NotNull XpathQuery query) {

        List<String> queryResults = queryContentAsList(query);

        String result;

        switch (queryResults.size()) {

            case 0:
                result = "";
                break;

            case 1:
                result = queryResults.get(0);
                break;

            default:
                String errorMessage = String.format("More than one results for Query '%s'", query.asString());
                throw new IllegalArgumentException(errorMessage);

        }

        return result;

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
