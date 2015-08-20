package eu.icarus.momca.momcapi.model;


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
public class MomcaResource {

    @NotNull
    private final String parentUri;
    @NotNull
    private final String resourceName;
    @NotNull
    private final Document xmlDocument;

    /**
     * Instantiates a new MomcaResource with an existing resource.
     *
     * @param momcaResource The MomcaResource to use.
     */
    MomcaResource(@NotNull MomcaResource momcaResource) {
        this.resourceName = momcaResource.getResourceName();
        this.xmlDocument = momcaResource.toDocument();
        this.parentUri = momcaResource.getParentUri();
    }

    /**
     * Instantiates a new MomcaResource.
     *
     * @param resourceName        The name of the resource, e.g. {@code user.xmlDocument}.
     * @param parentCollectionUri The URI of the collection, the resource is stored in in the database,
     *                            e.g. {@code /db/mom-data/xrx.user}.
     * @param xmlDocument          The xmlDocument content of the resource as {@code String}.
     */
    public MomcaResource(@NotNull String resourceName, @NotNull String parentCollectionUri, @NotNull String xmlDocument) {

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
        return "MomcaResource{" +
                "resourceName='" + resourceName + '\'' +
                ", xmlDocument=" + xmlDocument +
                ", parentUri='" + parentUri + '\'' +
                '}';
    }

}
