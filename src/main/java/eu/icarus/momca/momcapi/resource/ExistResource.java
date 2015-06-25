package eu.icarus.momca.momcapi.resource;

import nu.xom.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by daniel on 24.06.2015.
 */
public class ExistResource {

    @NotNull
    private final String parentCollectionUri;
    @NotNull
    private final String resourceName;
    @NotNull
    private final Document xmlAsDocument;

    ExistResource(@NotNull final ExistResource existResource) {
        this.resourceName = existResource.getResourceName();
        this.xmlAsDocument = existResource.getXmlAsDocument();
        this.parentCollectionUri = existResource.getParentCollectionUri();
    }

    public ExistResource(@NotNull final String resourceName, @NotNull final String parentCollectionUri, @NotNull final String xmlContent) throws ParsingException, IOException {
        this.resourceName = resourceName;
        this.xmlAsDocument = parseXmlString(xmlContent);
        this.parentCollectionUri = parentCollectionUri;
    }

    @SafeVarargs
    @NotNull
    final List<String> queryContent(@NotNull String xpath, @Nullable AbstractMap.SimpleEntry<String, String>... namespaces) {

        List<String> results = new LinkedList<>();

        Element root = getXmlAsDocument().getRootElement();
        XPathContext context = XPathContext.makeNamespaceContext(root);
        if (namespaces != null) {
            for (AbstractMap.SimpleEntry<String, String> namespace : namespaces) {
                context.addNamespace(namespace.getKey(), namespace.getValue());
            }

        }

        Nodes nodes = getXmlAsDocument().getRootElement().query(xpath, context);
        for (int i = 0; i < nodes.size(); i++) {
            results.add(nodes.get(i).getValue());
        }

        return results;

    }

    @NotNull
    public String getParentCollectionUri() {
        return parentCollectionUri;
    }

    @NotNull
    public String getResourceName() {
        return resourceName;
    }

    @NotNull
    public String getResourceUri() {
        return parentCollectionUri + "/" + resourceName;
    }

    @NotNull
    public Document getXmlAsDocument() {
        return xmlAsDocument;
    }

    @Override
    public String toString() {
        return "ExistResource{" +
                "resourceName='" + resourceName + '\'' +
                ", xmlAsDocument=" + xmlAsDocument +
                ", parentCollectionUri='" + parentCollectionUri + '\'' +
                '}';
    }

    @NotNull
    private Document parseXmlString(@NotNull String xmlAsString) throws ParsingException, IOException {
        Builder parser = new Builder();
        return parser.build(xmlAsString, null);

    }

}
