package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.resource.CountryCode;
import eu.icarus.momca.momcapi.resource.ResourceRoot;
import eu.icarus.momca.momcapi.resource.User;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.atom.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates {@code ExistQuery} instances.
 *
 * @author Daniel Jeller
 *         Created on 07.03.2015.
 * @see ExistQuery
 */
public class ExistQueryFactory {

    private ExistQueryFactory() {
    }

    /**
     * @param resourceAtomId   The resource's {@code atom:id}.
     * @param resourceRoot The resource root of the resource the search should be restricted to. If {@code null},
     *                     the whole database is searched.
     * @return A query to check if a resource matching {@code resourceAtomId} is existing in the database at the
     * specified {@code resourceRoot}.
     */
    @NotNull
    public static ExistQuery checkResourceExistence(@NotNull AtomId resourceAtomId, @Nullable ResourceRoot resourceRoot) {

        String query = String.format(
                "%s collection('%s')//atom:entry[.//atom:id/text()='%s'][1]",
                getNamespaceDeclaration(Namespace.ATOM),
                getRootCollectionString(resourceRoot),
                resourceAtomId.getText());

        return new ExistQuery(query);

    }

    /**
     * @param code The content of the {@code eap:code} child-element of the eap-element tree to delete, e.g. {@code DE}.
     * @return A query to delete a eap-element tree, e.g.
     * {@code <eap:country><eap:code>DE</eap:code><eap:nativeform/></eap:country>} or
     * {@code <eap:subdivision><eap:code>DE-BW</eap:code><eap:nativeform/></eap:subdivision>}. It deletes the whole tree
     * including its sub-elements.
     */
    @NotNull
    public static ExistQuery deleteEapElement(@NotNull String code) {

        String query = String.format(
                "%s update delete doc('%s/mom.portal.xml')//eap:*[eap:code='%s']",
                getNamespaceDeclaration(Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                code);

        return new ExistQuery(query);
    }

    @NotNull
    public static ExistQuery getCountryNativeName(@NotNull CountryCode countryCode) {

        String query = String.format("%s distinct-values(" +
                        "(doc('%s/mom.portal.xml')//eap:country[./eap:code='%s']/eap:nativeform/text(),\n" +
                        "    data(collection('%s')//cei:country[@id='%s']/text())))",
                getNamespaceDeclaration(Namespace.CEI, Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                countryCode.getCode(),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                countryCode.getCode());

        return new ExistQuery(query);

    }

    /**
     * @return Returns the {@code xs:dateTime} (with timezone) from the database.
     */
    @NotNull
    public static ExistQuery getCurrentDateTime() {
        return new ExistQuery("current-dateTime()");
    }

    /**
     * @param code The code of the country, e.g. {@code DE}.
     * @return A query to get the complete XML content of the {@code eap:country}-element specified by {@code code}.
     */
    @NotNull
    public static ExistQuery getEapCountryXml(@NotNull String code) {

        String query = String.format(
                "%s doc('%s/mom.portal.xml')//eap:country[eap:code='%s']",
                getNamespaceDeclaration(Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                code);

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery getRegionCode(@NotNull String regionNativeName) {

        String query = String.format("%s distinct-values(" +
                        "(doc('%s/mom.portal.xml')//eap:subdivision[eap:nativeform = '%s']/eap:code/text(),\n" +
                        "    data(collection('%s')//cei:provenance[cei:region= '%s']/cei:region/@id)))",
                getNamespaceDeclaration(Namespace.CEI, Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                regionNativeName,
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                regionNativeName);

        return new ExistQuery(query);

    }

    /**
     * @param resourceAtomId   The {@code atom:id} of the resource to locate.
     * @param resourceRoot The resource root of the resource the search should be restricted to. If {@code null},
     *                     the whole database is searched.
     * @return A query to get the absolute URIs, e.g. {@code /db/mom-data/xrx.user/admin.xml}, of all resources
     * matching {@code resourceAtomId} in {@code ResourceRoot} in the database.
     */
    @NotNull
    public static ExistQuery getResourceUri(@NotNull AtomId resourceAtomId, @Nullable ResourceRoot resourceRoot) {

        String query = String.format(
                "%s let $nodes := collection('%s')//atom:entry[.//atom:id/text()='%s']" +
                        " for $node in $nodes" +
                        " return concat(util:collection-name($node), '/', util:document-name($node))",
                getNamespaceDeclaration(Namespace.ATOM),
                getRootCollectionString(resourceRoot),
                resourceAtomId.getText());

        return new ExistQuery(query);

    }

    /**
     * @param resourceUri         The URI of the resource to update.
     * @param qualifiedParentName The name of the parent, usually either {@code eap:country} or {@code eap:subdivision}.
     * @param code                The code of the element to append into, can be "" or null to target all elements.
     * @param elementToInsert     The XML code to append.
     * @return A query to append an eap element tree into all matching parent eap elements. The element is appended
     * after all other child-elements.
     */
    @NotNull
    public static ExistQuery insertEapElement(@NotNull String resourceUri, @NotNull String qualifiedParentName,
                                              @Nullable String code, @NotNull String elementToInsert) {

        String predicate = (code == null || code.isEmpty()) ? "" : String.format("/eap:country[eap:code='%s']", code);

        String query = String.format(
                "%s update insert %s into doc('%s')/%s/%s",
                getNamespaceDeclaration(qualifiedParentName),
                elementToInsert,
                resourceUri,
                predicate,
                qualifiedParentName);

        return new ExistQuery(query);

    }

    /**
     * @return A query that lists the ids of all archives in the database as strings.
     */
    @NotNull
    public static ExistQuery listArchives() {

        String query = String.format(
                "%s collection('%s')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.ARCHIVES.getUri());

        return new ExistQuery(query);

    }

    /**
     * @param countryCode The code of a country, e.g. {@code DE}.
     * @return A query to list all archives that use the country code in their XML.
     */
    @NotNull
    public static ExistQuery listArchivesForCountry(@NotNull CountryCode countryCode) {

        String query = String.format(
                "%s collection('%s')//atom:entry[.//eag:repositorid/@countrycode='%s']/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.EAG),
                ResourceRoot.ARCHIVES.getUri(),
                countryCode.getCode());

        return new ExistQuery(query);

    }

    /**
     * @param regionName The native name of a region, e.g. {@code Bayern}.
     * @return A query to list the ids of all archives that use the name of a region in their XML.
     */
    @NotNull
    public static ExistQuery listArchivesForRegion(@NotNull String regionName) {

        String query = String.format(
                "%s collection('%s')//atom:entry[.//eag:firstdem/text()='%s']/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.EAG),
                ResourceRoot.ARCHIVES.getUri(),
                regionName);

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listChartersImport(@NotNull IdFond idFond) {

        String query = String.format(
                "%s collection('%s/%s/%s')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.IMPORTED_ARCHIVAL_CHARTERS.getUri(),
                idFond.getArchiveIdentifier(),
                idFond.getFondIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listChartersImport(@NotNull IdCollection idCollection) {

        String query = String.format(
                "%s collection('%s/%s')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.IMPORTED_ARCHIVAL_CHARTERS.getUri(),
                idCollection.getIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listChartersPrivate(@NotNull IdMyCollection idMyCollection) {

        String query = String.format(
                "%s collection('%s')//atom:entry/atom:id/text()[contains(., 'charter') and contains(., '%s')]",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.USER_DATA.getUri(),
                idMyCollection.getIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listChartersPrivate(@NotNull User user) {

        String query = String.format(
                "%s collection('%s')//atom:entry[contains(./atom:id/text(), 'charter')][./atom:author/atom:email/text()='%s']/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.USER_DATA.getUri(),
                user.getUserName());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listChartersPublic(@NotNull IdFond idFond) {

        String query = String.format(
                "%s collection('%s/%s/%s')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.PUBLIC_CHARTERS.getUri(),
                idFond.getArchiveIdentifier(),
                idFond.getFondIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listChartersPublic(@NotNull IdCollection idCollection) {

        String query = String.format(
                "%s collection('%s/%s')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.PUBLIC_CHARTERS.getUri(),
                idCollection.getIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listChartersPublic(@NotNull IdMyCollection idMyCollection) {

        String query = String.format(
                "%s collection('%s/%s')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.PUBLIC_CHARTERS.getUri(),
                idMyCollection.getIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listChartersSaved() {

        String query = String.format(
                "%s collection('%s')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED.getUri());

        return new ExistQuery(query);

    }

    /**
     * @return A query that lists the ids of all charter collections in the database as strings.
     */
    @NotNull
    public static ExistQuery listCollections() {

        String query = String.format(
                "%s collection('%s')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri());

        return new ExistQuery(query);

    }

    /**
     * @param countryCode The code of a country, e.g. {@code DE}.
     * @return A query to list all collections that use the country code in their XML.
     */
    @NotNull
    public static ExistQuery listCollectionsForCountry(@NotNull CountryCode countryCode) {

        String query = String.format(
                "%s collection('%s')//atom:entry[.//cei:country/@id='%s']/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.CEI),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                countryCode.getCode());

        return new ExistQuery(query);

    }

    /**
     * @param regionName The native name of a region, e.g. {@code Bayern}.
     * @return A query to list the ids of all collections that use the name of a region in their XML.
     */
    @NotNull
    public static ExistQuery listCollectionsForRegion(@NotNull String regionName) {

        String query = String.format(
                "%s collection('%s')//atom:entry[.//cei:region/text()='%s']/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.CEI),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                regionName);

        return new ExistQuery(query);

    }

    /**
     * @return A query to get a list of the text content of all {@code eap:country/eap:code} elements. This is
     * effectively a list of all countries registered in the portal.
     */
    @NotNull
    public static ExistQuery listCountryCodes() {

        String query = String.format(
                "%s distinct-values((doc('%s/mom.portal.xml')//eap:country/eap:code[text() != '']/text(),\n" +
                        "    data(collection('%s')//cei:country[@id != '']/@id)))",
                getNamespaceDeclaration(Namespace.CEI, Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri());

        return new ExistQuery(query);

    }

    /**
     * @param idArchive The id of the archive the fonds to list need to belong to, e.g. {@code CH-KAE}
     * @return A query to list the ids of all fonds that belong to a specific archive.
     */
    @NotNull
    public static ExistQuery listFonds(@NotNull IdArchive idArchive) {

        String query = String.format(
                "%s collection('%s')//atom:id[contains(., '%s')]/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.ARCHIVAL_FONDS.getUri(),
                idArchive.getArchiveIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listRegionsNativeNames(@NotNull CountryCode countryCode) {

        String query = String.format("%s distinct-values((" +
                        "doc('%s/mom.portal.xml')//eap:country[eap:code = '%s']//eap:subdivision/eap:nativeform/text(),\n" +
                        "    data(collection('%s')//cei:provenance[cei:country/@id = '%s']/cei:region/text())))",
                getNamespaceDeclaration(Namespace.CEI, Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                countryCode.getCode(),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                countryCode.getCode());

        return new ExistQuery(query);

    }

    /**
     * @param resourceUri      The full uri of the resource, e.g. {@code /db/mom-data/xrx.user/admin.xml}.
     * @param elementToReplace The qualified name of the XML element to replace, e.g. {@code cei:idno} or {@code
     *                         element} (no namespace).
     * @param newElement       The new element content, e.g. {@code <cei:idno @id="ABC">Number 1</cei:idno>}.
     * @return A query to replace the first occurrence of an xml element with a new element in the specified resource.
     */
    @NotNull
    public static ExistQuery replaceFirstInResource(@NotNull String resourceUri, @NotNull String elementToReplace,
                                                    @NotNull String newElement) {

        String query = String.format(
                "%s update replace doc('%s')//%s[1] with %s",
                getNamespaceDeclaration(elementToReplace),
                resourceUri,
                elementToReplace,
                newElement);

        return new ExistQuery(query);

    }

    /**
     * @param resourceUri      The URI of the resource to update.
     * @param qualifiedElement The qualified name of the element to update, e.g. {@code eap:nativeform}.
     * @param currentText      The text content of the element to replace. If this is {@code null} or {@code ""}, the
     *                         returned query  targets all elements specified by {@code resourceUri} and
     *                         {@code qualifiedElement}.
     * @param newText          The text to replace currentText with.
     * @return A query to update the text content of all elements specified by {@code resourceUri},
     * {@code qualifiedElement} and {@code currentText} with {@code newText}.
     */
    @NotNull
    public static ExistQuery updateElementText(@NotNull String resourceUri, @NotNull String qualifiedElement,
                                               @Nullable String currentText, @NotNull String newText) {

        String predicate = (currentText == null || currentText.isEmpty())
                ? "" : String.format("[text()='%s']", currentText);
        String query = String.format(
                "%s update replace doc('%s')//%s%s/text() with '%s'",
                getNamespaceDeclaration(qualifiedElement),
                resourceUri,
                qualifiedElement,
                predicate,
                newText);

        return new ExistQuery(query);

    }

    @NotNull
    private static String getNamespaceDeclaration(@NotNull Namespace... namespaces) {

        StringBuilder declarationBuilder = new StringBuilder();

        for (Namespace namespace : namespaces) {
            declarationBuilder.append(String.format(
                    "declare namespace %s='%s';",
                    namespace.getPrefix(),
                    namespace.getUri()));
        }

        return declarationBuilder.toString();

    }

    @NotNull
    private static String getNamespaceDeclaration(@NotNull String... qualifiedElementNames) {

        List<Namespace> namespaceList = new ArrayList<>(0);

        for (String element : qualifiedElementNames) {
            String namespaceString = element.substring(0, element.indexOf(':')).toUpperCase();
            namespaceList.add(Namespace.valueOf(namespaceString));
        }

        return getNamespaceDeclaration(namespaceList.toArray(new Namespace[namespaceList.size()]));

    }

    @NotNull
    private static String getRootCollectionString(@Nullable ResourceRoot resourceRoot) {
        return (resourceRoot == null) ? "/db/mom-data" : (resourceRoot.getUri());
    }

}
