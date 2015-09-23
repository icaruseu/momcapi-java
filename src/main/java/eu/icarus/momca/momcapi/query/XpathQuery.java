package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.model.xml.Namespace;
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
    QUERY_CEI_ABSTRACT("//cei:abstract", Namespace.CEI),
    QUERY_CEI_BODY_IDNO_ID("//cei:body/cei:idno/@id", Namespace.CEI),
    QUERY_CEI_BODY_IDNO_TEXT("//cei:body/cei:idno/text()", Namespace.CEI),
    QUERY_CEI_BODY_IDNO_OLD("//cei:body/cei:idno/@old", Namespace.CEI),
    QUERY_CEI_CLASS("//cei:class/text()", Namespace.CEI),
    QUERY_CEI_COUNTRY_TEXT("//cei:country/text()", Namespace.CEI),
    QUERY_CEI_COUNTRY_ID("//cei:country/@id", Namespace.CEI),
    QUERY_CEI_DIPLOMATIC_ANALYSIS("//cei:diplomaticAnalysis", Namespace.CEI),
    QUERY_CEI_FRONT("//cei:country/@id", Namespace.CEI),
    QUERY_CEI_ISSUED_DATE("//cei:issued/cei:date", Namespace.CEI),
    QUERY_CEI_ISSUED_DATE_RANGE("//cei:issued/cei:dateRange", Namespace.CEI),
    QUERY_CEI_ISSUED_PLACE_NAME("//cei:issued/cei:placeName", Namespace.CEI),
    QUERY_CEI_IMAGE_SERVER_ADDRESS("//cei:image_server_address/text()", Namespace.CEI),
    QUERY_CEI_IMAGE_SERVER_FOLDER("//cei:image_server_folder/text()", Namespace.CEI),
    QUERY_CEI_ISSUED("//cei:issued", Namespace.CEI),
    QUERY_CEI_LANG_MOM("//cei:lang_MOM/text()", Namespace.CEI),
    QUERY_CEI_PROVENANCE_TEXT("//cei:provenance/text()", Namespace.CEI),
    QUERY_CEI_PROVENANCE_ABBR("//cei:provenance/@abbr", Namespace.CEI),
    QUERY_CEI_REGION_TEXT("//cei:region/text()", Namespace.CEI),
    QUERY_CEI_REGION_ID("//cei:region/@id", Namespace.CEI),
    QUERY_CEI_SOURCE_DESC("//cei:sourceDesc", Namespace.CEI),
    QUERY_CEI_TENOR("//cei:tenor", Namespace.CEI),
    QUERY_CEI_TEXT("//cei:text", Namespace.CEI),
    QUERY_CEI_WITNESS_ORIG_FIGURE("//cei:witnessOrig/cei:figure", Namespace.CEI),
    QUERY_EAD_BIBLIOGRAPHY("//ead:bibliography", Namespace.EAD),
    QUERY_EAD_BIOGHIST("//ead:bioghist", Namespace.EAD),
    QUERY_EAD_CUSTODHIST("//ead:custodhist", Namespace.EAD),
    QUERY_EAD_ODD("//ead:odd", Namespace.EAD),
    QUERY_EAD_UNITTITLE("//ead:unittitle/text()", Namespace.EAD),
    QUERY_EAG_AUTFORM("//eag:autform/text()", Namespace.EAG),
    QUERY_EAG_COUNTRYCODE("//eag:repositorid/@countrycode", Namespace.EAG),
    QUERY_EAG_DESC("//eag:desc", Namespace.EAG),
    QUERY_EAG_REPOSITORID("//eag:repositorid/text()", Namespace.EAG),
    QUERY_CONFIG_GROUP_NAME("//config:group/@name", Namespace.CONFIG),
    QUERY_CONFIG_NAME("//config:name", Namespace.CONFIG),
    QUERY_NAME("//name/text()"),
    QUERY_XRX_BOOKMARK("//xrx:bookmark/text()", Namespace.XRX),
    QUERY_XRX_EMAIL("//xrx:email/text()", Namespace.XRX),
    QUERY_XRX_IMAGE_ACCESS("//xrx:param[@name='image-access']/text()", Namespace.XRX),
    QUERY_XRX_DUMMY_IMAGE_URL("//xrx:param[@name='dummy-image-url']/text()", Namespace.XRX),
    QUERY_XRX_IMAGE_SERVER_BASE_URL("//xrx:param[@name='image-server-base-url']/text()", Namespace.XRX),
    QUERY_XRX_KEYWORD("//xrx:keyword/text()", Namespace.XRX),
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
