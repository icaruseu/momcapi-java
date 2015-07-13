package eu.icarus.momca.momcapi.resource;


import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
    private final String parentUri;
    @NotNull
    private final String resourceName;
    @NotNull
    private final Document xmlAsDocument;


    /**
     * Instantiates a new ExistResource with an existing resource.
     *
     * @param existResource The ExistResource to use.
     */
    ExistResource(@NotNull final ExistResource existResource) {
        this.resourceName = existResource.getResourceName();
        this.xmlAsDocument = existResource.getXmlAsDocument();
        this.parentUri = existResource.getParentUri();
    }

    /**
     * Instantiates a new ExistResource.
     *
     * @param resourceName        The name of the resource, e.g. {@code user.xml}.
     * @param parentCollectionUri The URI of the collection, the resource is stored in in the database, e.g. {@code /db/mom-data/xrx.user}.
     * @param xmlContent          The xml content of the resource as {@code String}.
     */
    public ExistResource(@NotNull final String resourceName, @NotNull final String parentCollectionUri, @NotNull final String xmlContent) {

        try {

            this.resourceName = Util.encode(resourceName);
            this.xmlAsDocument = parseXmlString(xmlContent);
            this.parentUri = Util.encode(parentCollectionUri);

        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to create ExistResource for '%s'", resourceName), e);
        } catch (ParsingException e) {
            throw new IllegalArgumentException(String.format("Failed to parse the xml content of resource '%s'", resourceName), e);
        }

    }

    /**
     * Query the resource's XML content.
     *
     * @param query The Xpath Query to execute on the content.
     * @return The nodes containing the results.
     */
    @NotNull
    final Nodes listQueryResultNodes(@NotNull XpathQuery query) {

        Element root = getXmlAsDocument().getRootElement();
        XPathContext context = XPathContext.makeNamespaceContext(root);
        query.getNamespaces().forEach(n -> context.addNamespace(n.getPrefix(), n.getUri()));
        return getXmlAsDocument().getRootElement().query(query.getQuery(), context);

    }

    /**
     * The Xpath Query to execute on the content.
     *
     * @param query the query
     * @return A list of the results as strings.
     */
    @NotNull
    final List<String> listQueryResultStrings(@NotNull XpathQuery query) {

        Nodes nodes = listQueryResultNodes(query);
        List<String> results = new LinkedList<>();
        for (int i = 0; i < nodes.size(); i++) {
            results.add(nodes.get(i).getValue());
        }
        return results;

    }

    /**
     * @return The URI of the collection, the resource is stored in in the database, e.g. {@code /db/mom-data/xrx.user}.
     */
    @NotNull
    public String getParentUri() {
        return parentUri;
    }

    /**
     * @return The name of the resource in the database, e.g. {@code user.xml}
     */
    @NotNull
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @return The URI of the resource in the database, e.g. {@code /db/mom-data/xrx.user/user.xml}.
     */
    @NotNull
    public String getUri() {
        return parentUri + "/" + resourceName;
    }

    /**
     * @return The XML as an XML document.
     */
    @NotNull
    public Document getXmlAsDocument() {
        return xmlAsDocument;
    }

    @NotNull
    @Override
    public String toString() {
        return "ExistResource{" +
                "resourceName='" + resourceName + '\'' +
                ", xmlAsDocument=" + xmlAsDocument +
                ", parentUri='" + parentUri + '\'' +
                '}';
    }

    @NotNull
    private Document parseXmlString(@NotNull String xmlAsString) throws ParsingException, IOException {
        Builder parser = new Builder();
        return parser.build(xmlAsString, null);
    }

}
