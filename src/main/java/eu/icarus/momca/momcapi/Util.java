package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Utility functions.
 */
@SuppressWarnings({"ClassWithoutLogger", "PublicMethodWithoutLogging"})
public class Util {

    public static void changeNamespace(@NotNull Element xml, @NotNull Namespace namespace) {

        xml.setNamespaceURI(namespace.getUri());
        xml.setNamespacePrefix(namespace.getPrefix());

        Elements childs = xml.getChildElements();

        for (int i = 0; i < childs.size(); i++) {
            changeNamespace(childs.get(i), namespace);
        }

    }

    /**
     * Decodes a string that is %-encoded, e.g. {@code user%40mail.com} gets decoded to {@code user@mail.com}.
     *
     * @param string The string to decode.
     * @return The decoded string.
     */
    @NotNull
    public static String decode(@NotNull String string) {

        List<String> decodedTokens = new ArrayList<>(0);
        for (String token : string.split("/")) {
            try {
                decodedTokens.add(URLDecoder.decode(token, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return String.join("/", decodedTokens);

    }

    /**
     * %-encodes a string, e.g. {@code user@mail.com} gets encoded to {@code user%40mail.com}.
     *
     * @param string The string to encode.
     * @return The encoded string.
     */
    @NotNull
    public static String encode(@NotNull String string) {

        List<String> encodedTokens = new ArrayList<>(0);
        for (String token : string.split("/")) {

            if (token.equals(AtomId.DEFAULT_PREFIX)) {
                encodedTokens.add(token);
            } else {
                try {
                    encodedTokens.add(URLEncoder.encode(URLDecoder.decode(token, "UTF-8"), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return String.join("/", encodedTokens);

    }

    public static List<Node> getChildNodes(@NotNull Element element) {

        List<Node> nodes = new ArrayList<>(0);

        for (int i = 0; i < element.getChildCount(); i++) {
            nodes.add(element.getChild(i));
        }

        return nodes;

    }

    /**
     * @param uri An URI.
     * @return The last URI part, e.g. {@code admin.xml} for {@code /db/mom-data/user.xrx/admin.xml}
     */
    @SuppressWarnings("AccessCanBeTightened")
    @NotNull
    public static String getLastUriPart(@NotNull String uri) {
        testIfUri(uri);
        return uri.substring(uri.lastIndexOf('/') + 1, uri.length());
    }

    /**
     * @param uri An URI.
     * @return The parent of the last URI item, e.g. {@code /db/mom-data/user.xrx} for
     * {@code /db/mom-data/user.xrx/admin.xml}
     */
    @NotNull
    public static String getParentUri(@NotNull String uri) {
        testIfUri(uri);
        return uri.substring(0, uri.lastIndexOf('/'));
    }

    @NotNull
    public static Document getXmlFromResource(@NotNull String resourceName) throws ParsingException, IOException {

        try (InputStream is = Util.class.getClassLoader().getResourceAsStream(resourceName)) {
            Builder parser = new Builder();
            return parser.build(is);
        }

    }

    @NotNull
    private static XPathContext getxPathContext(@NotNull Element root, @NotNull XpathQuery query) {
        XPathContext context = XPathContext.makeNamespaceContext(root);
        query.getNamespaces().forEach(n -> context.addNamespace(n.getPrefix(), n.getUri()));
        return context;
    }

    /**
     * @param archIdentifierElement Element to test.
     * @return true if the provided Element has neither text content or child elements.
     */
    public static boolean isEmptyElement(Element archIdentifierElement) {
        return archIdentifierElement == null ||
                (archIdentifierElement.getValue().isEmpty() && archIdentifierElement.getChildElements().size() == 0);
    }

    /**
     * Tests if a ExistQuery result is signifying @code{true}.
     *
     * @param queryResults The result list which to process.
     * @return true if the queryResults @code{[true]}.
     */
    @SuppressWarnings("AccessCanBeTightened")
    public static boolean isTrue(List<String> queryResults) {
        return queryResults.size() == 1 & queryResults.get(0).equals("true");
    }

    @NotNull
    public static String joinChildNodes(@NotNull Element xml) {

        int childCount = xml.getChildCount();

        StringBuilder result = new StringBuilder("");

        for (int i = 0; i < childCount; i++) {
            result.append(xml.getChild(i).toXML());
        }

        return result.toString();

    }

    @NotNull
    public static Document parseToDocument(@NotNull String xml) {

        Builder builder = new Builder();
        try {

            return builder.build(replaceAmpersandInString(xml), null);

        } catch (ParsingException e) {
            throw new IllegalArgumentException("Failed to parse xml: " + xml, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @NotNull
    public static Element parseToElement(@NotNull String xml) {
        Document doc = parseToDocument(xml);
        return (Element) doc.getRootElement().copy();
    }

    /**
     * The Xpath Query to execute on the content.
     *
     * @param query the query
     * @return A list of the results as strings.
     */
    @NotNull
    public static List<String> queryXmlForList(@NotNull Element xml, @NotNull XpathQuery query) {

        List<Node> nodes = queryXmlForNodes(xml, query);
        List<String> results = new LinkedList<>();

        for (Node node : nodes) {

            if (node instanceof Element) {
                results.add(node.toXML());
            } else {
                results.add(node.getValue());
            }

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
    public static List<Node> queryXmlForNodes(@NotNull Element xml, @NotNull XpathQuery query) {

        String queryString = query.asString();
        XPathContext context = getxPathContext(xml, query);

        Nodes nodes = xml.query(queryString, context);

        List<Node> nodeList = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            nodeList.add(nodes.get(i));
        }

        return nodeList;

    }

    @NotNull
    public static Optional<Element> queryXmlForOptionalElement(@NotNull Element xml, @NotNull XpathQuery query) {

        Optional<Element> result = Optional.empty();

        List<Node> nodes = Util.queryXmlForNodes(xml, query);

        if (nodes.size() != 0) {
            result = Optional.of((Element) nodes.get(0));
        }

        return result;

    }

    @NotNull
    public static Optional<String> queryXmlForOptionalString(@NotNull Element xml, @NotNull XpathQuery query) {

        String queryResult = queryXmlForString(xml, query);
        Optional<String> result = Optional.empty();

        if (!queryResult.isEmpty()) {
            result = Optional.of(queryResult);
        }

        return result;

    }

    @NotNull
    public static String queryXmlForString(@NotNull Element xml, @NotNull XpathQuery query) {

        List<String> queryResults = queryXmlForList(xml, query);

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
     * Replace single ampersands in a string with the amp entity.
     * Doesn't replace ampersands part of quot amp apos lt gt and unicode entities.
     *
     * @param string The text to replace.
     * @return The resulting string.
     */
    @SuppressWarnings("AccessCanBeTightened")
    @NotNull
    public static String replaceAmpersandInString(@NotNull String string) {
        return string.replaceAll("&(?!(quot;)|(amp;)|(apos;)|(lt;)|(gt;)|(nbsp;)|(#\\d+;))", "&amp;");
    }

    private static void testIfUri(@NotNull String possibleUri) {

        if (!possibleUri.contains("/")) {
            String message = String.format("'%s' is probably not a valid uri, it doesn't contain '/'.", possibleUri);
            throw new IllegalArgumentException(message);
        }

    }

}