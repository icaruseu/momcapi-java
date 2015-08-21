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
    @Deprecated
    @NotNull
    private Document xmlDocument;

    /**
     * Instantiates a new ExistResource with an existing resource.
     *
     * @param existResource The ExistResource to use.
     */
    ExistResource(@NotNull ExistResource existResource) {
        this.resourceName = existResource.getResourceName();
        this.xmlDocument = existResource.toDocument();
        this.parentUri = existResource.getParentUri();
    }

    /**
     * Instantiates a new ExistResource.
     *
     * @param resourceName        The name of the resource, e.g. {@code user.xmlDocument}.
     * @param parentCollectionUri The URI of the collection, the resource is stored in in the database,
     *                            e.g. {@code /db/mom-data/xrx.user}.
     * @param xmlDocument         The xmlDocument content of the resource as {@code String}.
     */
    public ExistResource(@NotNull String resourceName, @NotNull String parentCollectionUri, @NotNull String xmlDocument) {

        if (resourceName.isEmpty() || parentCollectionUri.isEmpty() || xmlDocument.isEmpty()) {
            throw new IllegalArgumentException("Constructor strings are not allowed to be empty.");
        }

        this.resourceName = Util.encode(resourceName);
        this.xmlDocument = Util.parseToDocument(xmlDocument).getDocument();
        this.parentUri = Util.encode(parentCollectionUri);

    }

    /**
     * @return The URI of the collection, the resource is stored in in the database, e.g. {@code /db/mom-data/xrx.user}.
     */
    @NotNull
    public String getParentUri() {
        return parentUri;
    }

    /**
     * @return The name of the resource in the database, e.g. {@code user.xmlDocument}
     */
    @NotNull
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @return The URI of the resource in the database, e.g. {@code /db/mom-data/xrx.user/user.xmlDocument}.
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
    final Nodes queryContentAsNodes(@NotNull XpathQuery query) {

        Element rootElement = xmlDocument.getRootElement();
        String queryString = query.asString();
        XPathContext context = getxPathContext(rootElement, query);

        return rootElement.query(queryString, context);

    }

    @NotNull
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

    public void setParentUri(@NotNull String parentUri) {

        if (parentUri.isEmpty()) {
            throw new IllegalArgumentException("The parent URI is not allowed to be an empty string.");
        }
        this.parentUri = parentUri;

    }

    public void setResourceName(@NotNull String resourceName) {

        if (resourceName.isEmpty()) {
            throw new IllegalArgumentException("The resource name is not allowed to be an empty string.");
        }
        this.resourceName = resourceName;

    }

    /**
     * @return The XML as an XML document.
     */
    @NotNull
    public Document toDocument() {
        return xmlDocument;
    }

    @NotNull
    @Override
    public String toString() {
        return "ExistResource{" +
                "resourceName='" + resourceName + '\'' +
                ", xmlDocument=" + xmlDocument +
                ", parentUri='" + parentUri + '\'' +
                '}';
    }

}
