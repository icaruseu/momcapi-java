package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.Namespace;

/**
 * Created by daniel on 27.06.2015.
 */
public enum XpathQuery {

    QUERY_ATOM_ID("//atom:id/text()", Namespace.ATOM),
    QUERY_NAME("//name"),
    QUERY_XRX_BOOKMARK("//xrx:bookmark/text()", Namespace.XRX),
    QUERY_XRX_EMAIL("//xrx:email/text()", Namespace.XRX),
    QUERY_XRX_NAME("//xrx:name", Namespace.XRX),
    QUERY_XRX_SAVED("//xrx:saved/xrx:id/text()", Namespace.XRX);

    private final Namespace[] namespaces;
    private final String query;

    XpathQuery(String query, Namespace... namespaces) {
        this.query = query;
        this.namespaces = namespaces;
    }

    public Namespace[] getNamespaces() {
        return namespaces;
    }

    public String getQuery() {
        return query;
    }

}
