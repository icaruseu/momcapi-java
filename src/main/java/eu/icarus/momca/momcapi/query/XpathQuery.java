package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.XPathContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * An XPath-query to use to query XOM XML documents.
 *
 * @author Daniel Jeller
 *         Created on 27.06.2015.
 * @see nu.xom.Node#query(String, XPathContext)
 */
public enum XpathQuery {

    QUERY_ATOM_EMAIL("//atom:email/text()", Namespace.ATOM),
    QUERY_ATOM_ID("//atom:id/text()", Namespace.ATOM),
    QUERY_CEI_WITNESS_ORIG_FIGURE("//cei:witnessOrig/cei:figure", Namespace.CEI),
    QUERY_CEI_BODY_IDNO_ID("//cei:body/cei:idno/@id", Namespace.CEI),
    QUERY_CEI_BODY_IDNO_TEXT("//cei:body/cei:idno/text()", Namespace.CEI),
    QUERY_CEI_ISSUED("//cei:issued", Namespace.CEI),
    QUERY_CEI_TEXT("//cei:text", Namespace.CEI),
    QUERY_EAG_AUTFORM("//eag:autform/text()", Namespace.EAG),
    QUERY_EAG_DESC("//eag:desc", Namespace.EAG),
    QUERY_EAG_REPOSITORID("//eag:repositorid/text()", Namespace.EAG),
    QUERY_CONFIG_GROUP_NAME("//config:group/@name", Namespace.CONFIG),
    QUERY_CONFIG_NAME("//config:name", Namespace.CONFIG),
    QUERY_NAME("//name/text()"),
    QUERY_XRX_BOOKMARK("//xrx:bookmark/text()", Namespace.XRX),
    QUERY_XRX_EMAIL("//xrx:email/text()", Namespace.XRX),
    QUERY_XRX_MODERATOR("//xrx:moderator/text()", Namespace.XRX),
    QUERY_XRX_NAME("//xrx:name/text()", Namespace.XRX),
    QUERY_XRX_SAVED_ID("//xrx:saved/xrx:id/text()", Namespace.XRX);

    @NotNull
    private final List<Namespace> namespaces;
    private final String query;

    XpathQuery(String query, Namespace... namespaces) {
        this.query = query;
        this.namespaces = Arrays.asList(namespaces);
    }

    /**
     * @return The xpath query.
     */
    public String asString() {
        return query;
    }

    /**
     * @return A list of all namespaces used in the query.
     * @see Namespace
     */
    @NotNull
    public List<Namespace> getNamespaces() {
        return namespaces;
    }

}
