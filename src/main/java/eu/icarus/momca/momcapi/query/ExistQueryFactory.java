package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.resource.Charter;
import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
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
     * @param resourceAtomId The resource's {@code atom:id}.
     * @param resourceRoot   The resource root of the resource the search should be restricted to. If {@code null},
     *                       the whole database is searched.
     * @return A query to check if a resource matching {@code resourceAtomId} is existing in the database at the
     * specified {@code resourceRoot}.
     */
    @NotNull
    public static ExistQuery checkAtomResourceExistence(@NotNull AtomId resourceAtomId, @Nullable ResourceRoot resourceRoot) {

        String query = String.format(
                "%scollection('%s')//atom:id[./text()='%s'][1]",
                getNamespaceDeclaration(Namespace.ATOM),
                getRootCollectionString(resourceRoot),
                resourceAtomId.getText());

        return new ExistQuery(query);

    }

    /**
     * @param collectionUri The URI of the collection to test.
     * @return An ExistQuery that checks if a collection exists. When executed, the query returns @code{[true]} if the
     * collection exists.
     */
    @NotNull
    public static ExistQuery checkCollectionExistence(@NotNull String collectionUri) {

        String query = String.format(
                "xmldb:collection-available('%s')",
                collectionUri);

        return new ExistQuery(query);

    }

    /**
     * @param resourceUri The URI of the resource to check for.
     * @return An ExistQuery that checks whether or not a resource exists in the database. When executed, the query
     * returns @code{[true]} if the resource exists.
     */
    @NotNull
    public static ExistQuery checkExistResourceExistence(@NotNull String resourceUri) {

        String query = String.format(
                "exists(doc('%s'))",
                resourceUri);

        return new ExistQuery(query);

    }

    /**
     * @param idMyCollection     The id of the myCollection to test for.
     * @param myCollectionStatus The status of the mycollection.
     * @return An ExistQuery that checks if a collection exists. When executed, the query returns @code{[true]}
     * if the collection exists.
     */
    @NotNull
    public static ExistQuery checkMyCollectionExistence(@NotNull IdMyCollection idMyCollection,
                                                        @NotNull MyCollectionStatus myCollectionStatus) {

        String query = String.format(
                "%sexists(collection('%s')//atom:entry[.//atom:id/text()='%s'])",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.CEI),
                myCollectionStatus == MyCollectionStatus.PRIVATE ?
                        ResourceRoot.USER_DATA.getUri() : ResourceRoot.PUBLISHED_USER_COLLECTIONS.getUri(),
                idMyCollection.getAtomId());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery checkUserExistence(@NotNull IdUser idUser) {

        String query = String.format(
                "xmldb:exists-user('%s')",
                idUser.getIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery createCollection(@NotNull String parentUri, @NotNull String name) {

        String query = String.format(
                "xmldb:create-collection('%s', '%s')",
                parentUri,
                name);

        return new ExistQuery(query);

    }

    /**
     * @param eapText The text value that signifies which element to delete. Can be either a {@code eap:code} or
     *                {@code eap:nativeform}
     * @return A query to delete a eap-element tree, e.g.
     * {@code <eap:country><eap:code>DE</eap:code><eap:nativeform/></eap:country>} or
     * {@code <eap:subdivision><eap:code>DE-BW</eap:code><eap:nativeform/></eap:subdivision>}. It deletes the whole tree
     * including its sub-elements. When executed, the query returns @code{[true]}.
     */
    @NotNull
    public static ExistQuery deleteEapElement(@NotNull String eapText) {

        String query = String.format(
                "%s(update delete doc('%s/mom.portal.xml')//eap:*[eap:code='%s' or eap:nativeform='%s'], " +
                        "not(exists( doc('%s/mom.portal.xml')//eap:*[eap:code='%s' or eap:nativeform='%s'])))",
                getNamespaceDeclaration(Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                eapText,
                eapText,
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                eapText,
                eapText);

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
                "%sdoc('%s/mom.portal.xml')//eap:country[eap:code='%s']",
                getNamespaceDeclaration(Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                code);

        return new ExistQuery(query);

    }

    @NotNull
    private static String getNamespaceDeclaration(@NotNull Namespace... namespaces) {

        StringBuilder declarationBuilder = new StringBuilder();

        for (Namespace namespace : namespaces) {
            declarationBuilder.append(String.format(
                    "declare namespace %s='%s'; ",
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
    public static ExistQuery getRegionCode(@NotNull String regionNativeName) {

        String query = String.format(
                "%sdistinct-values(" +
                        "(doc('%s/mom.portal.xml')//eap:subdivision[eap:nativeform = '%s']/eap:code/text(),\n" +
                        "    data(collection('%s')//cei:provenance[cei:region= '%s']/cei:region/@id)))",
                getNamespaceDeclaration(Namespace.CEI, Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                regionNativeName,
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                regionNativeName);

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery getResource(@NotNull String uri) {

        String query = String.format(
                "doc('%s')",
                uri
        );

        return new ExistQuery(query);

    }

    /**
     * @param resourceAtomId The {@code atom:id} of the resource to locate.
     * @param resourceRoot   The resource root of the resource the search should be restricted to. If {@code null},
     *                       the whole database is searched.
     * @return A query to get the absolute URIs, e.g. {@code /db/mom-data/xrx.user/admin.xml}, of all resources
     * matching {@code resourceAtomId} in {@code ResourceRoot} in the database.
     */
    @NotNull
    public static ExistQuery getResourceUri(@NotNull AtomId resourceAtomId, @Nullable ResourceRoot resourceRoot) {

        String query = String.format(
                "%slet $nodes := collection('%s')//atom:entry[.//atom:id/text()='%s']\n" +
                        " for $node in $nodes\n" +
                        " return concat(util:collection-name($node), '/', util:document-name($node))",
                getNamespaceDeclaration(Namespace.ATOM),
                getRootCollectionString(resourceRoot),
                resourceAtomId.getText());

        return new ExistQuery(query);

    }

    @NotNull
    private static String getRootCollectionString(@Nullable ResourceRoot resourceRoot) {
        return (resourceRoot == null) ? "/db/mom-data" : (resourceRoot.getUri());
    }

    /**
     * @param resourceUri         The URI of the resource to update.
     * @param qualifiedParentName The name of the parent, usually either {@code eap:country} or {@code eap:subdivision}.
     * @param code                The code of the element to append into, can be "" or null to target all elements.
     * @param elementToInsert     The XML code to append.
     * @return A query to append an eap element tree into all matching parent eap elements. The element is appended
     * after all other child-elements. When executed, the query returns @code{[true]}.
     */
    @NotNull
    public static ExistQuery insertEapElement(@NotNull String resourceUri, @NotNull String qualifiedParentName,
                                              @Nullable String code, @NotNull String elementToInsert) {

        String predicate = (code == null || code.isEmpty()) ? "" : String.format("/eap:country[eap:code='%s']", code);

        String query = String.format(
                "%s(update insert %s into doc('%s')/%s/%s, exists(doc('%s')//%s))",
                getNamespaceDeclaration(qualifiedParentName),
                elementToInsert,
                resourceUri,
                predicate,
                qualifiedParentName,
                resourceUri,
                elementToInsert);

        return new ExistQuery(query);

    }

    /**
     * @return A query that lists the ids of all archives in the database as strings.
     */
    @NotNull
    public static ExistQuery listArchives() {

        String query = String.format(
                "%scollection('%s')//atom:id/text()",
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
                "%scollection('%s')//atom:entry[.//eag:repositorid/@countrycode='%s']/atom:id/text()",
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
                "%scollection('%s')//atom:entry[.//eag:firstdem/text()='%s']/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.EAG),
                ResourceRoot.ARCHIVES.getUri(),
                regionName);

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listCharterAtomIds(@NotNull IdAbstract parent, @NotNull CharterStatus status) {

        String rootUrl = status.getResourceRoot().getUri();
        String parentSelector = "";
        String userSelector = "";

        if (parent instanceof IdFond) {

            IdFond idFond = (IdFond) parent;
            parentSelector = String.format(".*(%s/%s)", idFond.getIdArchive().getIdentifier(), idFond.getIdentifier());

        } else if (parent instanceof IdUser) {

            userSelector = String.format("[./following-sibling::atom:author/atom:email='%s']", parent.getIdentifier());

        } else {

            parentSelector = ".*(" + parent.getIdentifier() + ")";

        }

        String query = String.format(
                "%scollection('%s')//atom:id[matches(., '(^tag:www\\.monasterium\\.net,2011:/charter)%s')]%s/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                rootUrl,
                parentSelector,
                userSelector);

        return new ExistQuery(query);

    }

    /**
     * @return A query that lists the ids of all charter collections in the database as strings.
     */
    @NotNull
    public static ExistQuery listCollections() {

        String query = String.format(
                "%scollection('%s')//atom:id/text()",
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
                "%scollection('%s')//atom:entry[.//cei:country/@id='%s']/atom:id/text()",
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
                "%scollection('%s')//atom:entry[.//cei:region/text()='%s']/atom:id/text()",
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
                "%sdistinct-values((doc('%s/mom.portal.xml')//eap:country/eap:code[text() != '']/text(),\n" +
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
                "%scollection('%s/%s')//atom:id[contains(., '%s')][not(contains(util:document-name(.),'.ead.old.'))]/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                ResourceRoot.ARCHIVAL_FONDS.getUri(),
                idArchive.getIdentifier(),
                idArchive.getIdentifier());

        return new ExistQuery(query);

    }

    public static ExistQuery listMyCollectionsPrivate(IdUser idUser) {

        String query = String.format(
                "%scollection('/db/mom-data/xrx.user/%s/metadata.mycollection')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                idUser.getIdentifier());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listMyCollectionsPublic() {

        String query = String.format(
                "%scollection('/db/mom-data/metadata.mycollection.public')//atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM));

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listRegionsNativeNames(@NotNull CountryCode countryCode) {

        String query = String.format(
                "%sdistinct-values((" +
                        "doc('%s/mom.portal.xml')//eap:country[eap:code = '%s']//eap:subdivision/eap:nativeform/text(),\n" +
                        "    data(collection('%s')//cei:provenance[cei:country/@id = '%s']/cei:region/text())))",
                getNamespaceDeclaration(Namespace.CEI, Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                countryCode.getCode(),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                countryCode.getCode());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery listUserIds() {

        String query = String.format(
                "%scollection('/db/mom-data/xrx.user')//xrx:email/text()",
                getNamespaceDeclaration(Namespace.XRX));

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery moveResource(@NotNull String sourceCollectiontUri, @NotNull String targetCollectionUri,
                                          @NotNull String targetFileName, @Nullable String originalFileName) {

        String query;

        if (originalFileName == null || originalFileName.isEmpty() || originalFileName.equals(targetFileName)) {

            query = String.format(
                    "xmldb:move('%s', '%s', '%s')",
                    sourceCollectiontUri,
                    targetCollectionUri,
                    targetFileName);

        } else {

            query = String.format(
                    "(xmldb:move('%s', '%s', '%s'), xmldb:rename('%s', '%s', '%s'))",
                    sourceCollectiontUri,
                    targetCollectionUri,
                    originalFileName,
                    targetCollectionUri,
                    originalFileName,
                    targetFileName);

        }

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery publishCharter(@NotNull IdCharter idCharter) {

        String publishedParentUri = Charter.createParentUri(idCharter, CharterStatus.PUBLIC, null);
        String savedFileName = Charter.createResourceName(idCharter, CharterStatus.SAVED);
        String publishedFileName = Charter.createResourceName(idCharter, CharterStatus.PUBLIC);

        String query = String.format(
                "(xmldb:move('%s', '%s', '%s'), " +
                        "xmldb:remove('%s', '%s'), " +
                        "xmldb:rename('%s', '%s', '%s'))",
                ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED.getUri(),
                publishedParentUri,
                savedFileName,
                publishedParentUri,
                publishedFileName,
                publishedParentUri,
                savedFileName,
                publishedFileName);

        return new ExistQuery(query);

    }

    /**
     * @param collectionUri The URI of the collection to remove
     * @return An ExistQuery that removes a collection from the database.
     * The executed query returns @code{[true]} if the deletion was successful.
     */
    public static ExistQuery removeCollection(@NotNull String collectionUri) {

        String query = String.format(
                "(xmldb:remove('%s')," +
                        "not(exists(collection('%s'))))",
                collectionUri,
                collectionUri);

        return new ExistQuery(query);

    }

    /**
     * @param existResource The resource to remove.
     * @return An ExistQuery that removes a resource from the database.
     * The executed query returns @code{[true]} if the deletion was successful.
     */
    public static ExistQuery removeResource(@NotNull ExistResource existResource) {

        String parentUri = existResource.getParentUri();
        String name = existResource.getResourceName();

        String query = String.format(
                "(xmldb:remove('%s', '%s')," +
                        "not(exists(doc('%s'))))",
                parentUri,
                name,
                existResource.getUri());

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery storeResource(@NotNull ExistResource existResource) {

        String collectionUri = existResource.getParentUri();
        String resourceName = existResource.getResourceName();
        String resourceContent = existResource.toXML();

        String query = String.format(
                "xmldb:store('%s', '%s', '%s')",
                collectionUri,
                resourceName,
                resourceContent);

        return new ExistQuery(query);

    }

    @NotNull
    public static ExistQuery updateCharterAtomId(@NotNull String parentUri, @NotNull String oldAtomId,
                                                 @NotNull String newAtomId, @NotNull String newDocumentName) {

        String query = String.format(
                "%slet $oldAtomIdNode := collection('%s')//atom:id[text() = '%s']\n" +
                        "let $oldDocumentName := util:document-name($oldAtomIdNode)\n" +
                        "return (\n" +
                        "    update value $oldAtomIdNode/text() with '%s',\n" +
                        "    xmldb:rename('%s', $oldDocumentName, '%s')\n" +
                        ")",
                getNamespaceDeclaration(Namespace.ATOM),
                parentUri,
                oldAtomId,
                newAtomId,
                parentUri,
                newDocumentName);

        return new ExistQuery(query);

    }

    /**
     * @param charter The charter to update.
     * @return An ExistQuery that updates the CEI - content of an existing charter. The ATOM-content will *not* be
     * updated apart from atom:updated. The executed query returns @code{[0]} if atom:updated after the updates
     * equals the current time at the update, meaning the update was successful), otherwise @code{[1]}
     */
    @NotNull
    public static ExistQuery updateCharterContent(@NotNull Charter charter) {

        String query = String.format(
                "%s\n" +
                        "let $charter := doc('%s')\n" +
                        "let $replacement := %s\n" +
                        "let $currentTime :=  current-dateTime()\n" +
                        "return (update replace $charter//cei:text with $replacement," +
                        "        update replace $charter//atom:updated/text() with $currentTime," +
                        "        compare(doc('%s')//atom:updated/text(), $currentTime))",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.CEI),
                charter.getUri(),
                charter.toCei().toXML(),
                charter.getUri());

        return new ExistQuery(query);

    }


}
