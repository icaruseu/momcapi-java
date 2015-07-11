package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.xml.Namespace;

import java.util.Arrays;
import java.util.List;

/**
 * Created by daniel on 27.06.2015.
 */
public enum XpathQuery {

    QUERY_ATOM_EMAIL("//atom:email/text()", Namespace.ATOM),
    QUERY_ATOM_ID("//atom:id/text()", Namespace.ATOM),
    QUERY_CEI_WITNESS_ORIG_FIGURE("//cei:witnessOrig/cei:figure", Namespace.CEI),
    QUERY_CEI_BODY_IDNO_ID("//cei:body/cei:idno/@id", Namespace.CEI),
    QUERY_CEI_BODY_IDNO_TEXT("//cei:body/cei:idno/text()", Namespace.CEI),
    QUERY_CEI_ISSUED("//cei:issued", Namespace.CEI),
    QUERY_CEI_TEXT("//cei:text", Namespace.CEI),
    QUERY_CONFIG_GROUP_NAME("//config:group/@name", Namespace.CONFIG),
    QUERY_CONFIG_NAME("//config:name", Namespace.CONFIG),
    QUERY_NAME("//name/text()"),
    QUERY_XRX_BOOKMARK("//xrx:bookmark/text()", Namespace.XRX),
    QUERY_XRX_EMAIL("//xrx:email/text()", Namespace.XRX),
    QUERY_XRX_MODERATOR("//xrx:moderator/text()", Namespace.XRX),
    QUERY_XRX_NAME("//xrx:name/text()", Namespace.XRX),
    QUERY_XRX_SAVED_ID("//xrx:saved/xrx:id/text()", Namespace.XRX);

    private final List<Namespace> namespaces;
    private final String query;

    XpathQuery(String query, Namespace... namespaces) {
        this.query = query;
        this.namespaces = Arrays.asList(namespaces);
    }

    public List<Namespace> getNamespaces() {
        return namespaces;
    }

    public String getQuery() {
        return query;
    }

}
