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
@SuppressWarnings({"ClassWithoutLogger", "PublicMethodWithoutLogging"})
public class ExistQueryFactory {

    private ExistQueryFactory() {
    }

    /**
     * @param idUser The user to check.
     * @return An ExistQuery that checks if an eXist account exists for an MOM-CA user. When executed, the database
     * returns <code>true</code> if the account exists.
     */
    @NotNull
    public static ExistQuery checkAccountExistence(@NotNull IdUser idUser) {

        String query = String.format(
                "xmldb:exists-user('%s')",
                idUser.getIdentifier());

        return new ExistQuery(query);

    }

    /**
     * @param resourceAtomId The resource's {@code atom:id}.
     * @param resourceRoot   The resource root of the resource the search should be restricted to. If <code>null</code>,
     *                       the whole database is searched.
     * @return A query to check if a resource matching <code>resourceAtomId</code> is existing in the database at the
     * specified <code>ResourceRoot</code>. When executed, the database returns the atom:id of the resource.
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
     * @return An ExistQuery that checks if a collection exists. When executed, the database returns <code>true</code>
     * if the collection exists.
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
     * @return An ExistQuery that checks whether or not a resource exists in the database. When executed, the database
     * returns <code>true</code> if the resource exists.
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
     * @return An ExistQuery that checks if a collection exists. When executed, the database returns
     * <code>true</code> if the collection exists.
     */
    @NotNull
    public static ExistQuery checkMyCollectionExistence(@NotNull IdMyCollection idMyCollection,
                                                        @NotNull MyCollectionStatus myCollectionStatus) {

        String query = String.format(
                "%sexists(collection('%s')//atom:id[.='%s'])",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.CEI),
                myCollectionStatus == MyCollectionStatus.PRIVATE ?
                        ResourceRoot.USER_DATA.getUri() : ResourceRoot.PUBLISHED_USER_COLLECTIONS.getUri(),
                idMyCollection.getAtomId());

        return new ExistQuery(query);

    }

    /**
     * @param parentUri The URI of the parent collection.
     * @param name      The name of the collection to create.
     * @return An ExistQuery that creates a child collection in the collection specified. When executed, the database
     * returns the URI of the collection created.
     */
    @NotNull
    public static ExistQuery createCollection(@NotNull String parentUri, @NotNull String name) {

        String query = String.format(
                "xmldb:create-collection('%s', '%s')",
                parentUri,
                name);

        return new ExistQuery(query);

    }

    /**
     * @param absoluteUri The absolute uri of the last collection on the path to create,
     *                    e.g. <code>/db/path/to/create</code>
     * @return An ExistQuery that creates all collections on a given path. When executed, the database returns either
     * <code>true</code> if the path already existed or a list of all created paths.
     */
    @NotNull
    public static ExistQuery createCollectionPath(@NotNull String absoluteUri) {

        String pathtokens = String.format("('%s')", absoluteUri.substring(1).replace("/", "','"));

        String query = String.format(
                "let $pathtokens := %s\n" +
                        "return if(exists(collection(concat('/', string-join($pathtokens, '/'))))) then \"true\"\n" +
                        "else\n" +
                        "    for $token at $i in $pathtokens[position()< count($pathtokens)]\n" +
                        "    let $parent := concat('/', string-join($pathtokens[position() <= $i], '/'))\n" +
                        "    return xmldb:create-collection($parent, $pathtokens[$i +1])",
                pathtokens
        );

        return new ExistQuery(query);

    }

    /**
     * @param eapText The text value that signifies which element to delete. Can be either a {@code eap:code} or
     *                {@code eap:nativeform}
     * @return A query to delete a eap-element tree, e.g.
     * {@code <eap:country><eap:code>DE</eap:code><eap:nativeform/></eap:country>} or
     * {@code <eap:subdivision><eap:code>DE-BW</eap:code><eap:nativeform/></eap:subdivision>}. It deletes the whole tree
     * including its sub-elements. When executed, the database returns <code>true</code> if the action succeeded.
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

    /**
     * @param countryCode The code of the country.
     * @return An ExistQuery that queries the native name of a country.
     */
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
     * @return Returns the <code>xs:dateTime</code> (with timezone) from the database.
     */
    @NotNull
    public static ExistQuery getCurrentDateTime() {
        return new ExistQuery("current-dateTime()");
    }

    /**
     * @param code The code of the country, e.g. <code>DE</code>.
     * @return A query to get the complete XML content of the <code>eap:country<</code>-element specified by the code.
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

    /**
     * @param regionNativeName The native name of the region.
     * @return An ExistQuery that gets the region code of a region from the database.
     */
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

    /**
     * @param uri The URI of the resource to get.
     * @return An ExistQuery that gets the content of a resource from the database.
     */
    @NotNull
    public static ExistQuery getResource(@NotNull String uri) {

        String query = String.format(
                "doc('%s')",
                uri
        );

        return new ExistQuery(query);

    }

    /**
     * @param resourceAtomId The <code>atom:id</code> of the resource to locate.
     * @param resourceRoot   The resource root of the resource the search should be restricted to. If <code>null</code>,
     *                       the whole database is searched.
     * @return An ExistQuery to get the absolute URIs, e.g. <code>/db/mom-data/xrx.user/admin.xml</code>, of all resources
     * matching <code>resourceAtomId</code> in <code>ResourceRoot</code> in the database.
     */
    @NotNull
    public static ExistQuery getResourceUri(@NotNull AtomId resourceAtomId, @Nullable ResourceRoot resourceRoot) {

        String query = String.format(
                "%slet $nodes := collection('%s')//atom:id[./text()='%s']\n" +
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
     * @param qualifiedParentName The name of the parent, usually either <code>eap:country</code>
     *                            or <code>eap:subdivision</code>.
     * @param code                The code of the element to append into, can be "" or null to target all elements.
     * @param elementToInsert     The XML code to append.
     * @return An ExistQuery to append an eap element tree into all matching parent eap elements. The element is appended
     * after all other child-elements. When executed, the database returns <code>true</code>.
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
     * @return An ExistQuery that lists the <code>atom:id</code>s of all archives in the database.
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
     * @param countryCode The code of a country, e.g. <code>DE</code>.
     * @return An ExistQuery to list the <code>atom:id</code>s of all archives that use the country code in their XML.
     */
    @NotNull
    public static ExistQuery listArchivesForCountry(@NotNull CountryCode countryCode) {

        String query = String.format(
                "%scollection('%s')//eag:repositorid[@countrycode='%s']/ancestor::atom:entry/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.EAG),
                ResourceRoot.ARCHIVES.getUri(),
                countryCode.getCode());

        return new ExistQuery(query);

    }

    /**
     * @param regionName The native name of a region, e.g. <code>Bayern</code>.
     * @return An ExistQuery to list the <code>atom:id</code>s of all archives that use the name of a region in their XML.
     */
    @NotNull
    public static ExistQuery listArchivesForRegion(@NotNull String regionName) {

        String query = String.format(
                "%scollection('%s')//eag:firstdem[./text()='%s']/ancestor::atom:entry/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.EAG),
                ResourceRoot.ARCHIVES.getUri(),
                regionName);

        return new ExistQuery(query);

    }

    /**
     * @param parent The parent, e.g. an archive, fond or user.
     * @param status The status of the charter instances, e.g. public or saved.
     * @return An ExistQuery that lists the <code>atom:id</code>s of all charter instances associated
     * with parent and status.
     */
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
     * @return An ExistQuery that lists the <code>atom:id</code>s of all charter collections in the database.
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
     * @param countryCode The code of a country, e.g. <code>DE</code>.
     * @return An ExistQuery to list the <code>atom:id</code>s of all collections that use the country code in their XML.
     */
    @NotNull
    public static ExistQuery listCollectionsForCountry(@NotNull CountryCode countryCode) {

        String query = String.format(
                "%scollection('%s')//cei:country[@id='%s']/ancestor::atom:entry/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.CEI),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                countryCode.getCode());

        return new ExistQuery(query);

    }

    /**
     * @param regionName The native name of a region, e.g. <code>Bayern</code>.
     * @return An ExistQuery to list the <code>atom:id</code>s of all collections that use the name of a region in their XML.
     */
    @NotNull
    public static ExistQuery listCollectionsForRegion(@NotNull String regionName) {

        String query = String.format(
                "%scollection('%s')//cei:region[./text()='%s']/ancestor::atom:entry/atom:id/text()",
                getNamespaceDeclaration(Namespace.ATOM, Namespace.CEI),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                regionName);

        return new ExistQuery(query);

    }

    /**
     * @return An ExistQuery to get a list of the text content of all <code>eap:country/eap:code</code> elements.
     * This is effectively a list of all countries registered in the portal.
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
     * @param idArchive The id of the archive the fonds to list need to belong to, e.g. <code>CH-KAE</code>
     * @return An ExistQuery to list the <code>atom:id</code>s of all fonds that belong to a specific archive.
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

    /**
     * @param status The status to associate with.
     * @param owner  The user to associate with. If <code>null</code>, the owner of the myCollection is not taken
     *               into consideration.
     * @return An ExistQuery that lists the <code>atom:id</code>s of all myCollections in the database that are
     * associated with a specific status and user.
     */
    @NotNull
    public static ExistQuery listMyCollections(@NotNull MyCollectionStatus status, @Nullable IdUser owner) {

        String authorSelector = owner == null
                ? "" : String.format("atom:email[.='%s']/parent::atom:author/preceding-sibling::", owner.getIdentifier());

        String query = String.format(
                "%scollection('%s')//%satom:id" +
                        "[matches(., '^tag:www.monasterium.net,2011:/mycollection/')]/text()",
                getNamespaceDeclaration(Namespace.ATOM),
                status.getResourceRoot().getUri(),
                authorSelector
        );

        return new ExistQuery(query);

    }

    /**
     * @param code The code of the country.
     * @return An ExistQuery that lists the native names of all regions associated with a specific country.
     */
    @NotNull
    public static ExistQuery listRegionsNativeNames(@NotNull CountryCode code) {

        String query = String.format(
                "%sdistinct-values((" +
                        "doc('%s/mom.portal.xml')//eap:country[eap:code = '%s']//eap:subdivision/eap:nativeform/text(),\n" +
                        "    data(collection('%s')//cei:provenance[cei:country/@id = '%s']/cei:region/text())))",
                getNamespaceDeclaration(Namespace.CEI, Namespace.EAP),
                ResourceRoot.PORTAL_HIERARCHY.getUri(),
                code.getCode(),
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(),
                code.getCode());

        return new ExistQuery(query);

    }

    /**
     * @return An ExistQuery that lists the ids of all users in the database.
     */
    @NotNull
    public static ExistQuery listUserIds() {

        String query = String.format(
                "%scollection('/db/mom-data/xrx.user')//xrx:email/text()",
                getNamespaceDeclaration(Namespace.XRX));

        return new ExistQuery(query);

    }

    /**
     * @param sourceCollectiontUri The collection the resource resides in.
     * @param targetCollectionUri  The target collection to move the resource to.
     * @param targetFileName       If not <code>null</code>, the name the resource will be renamed to after moving.
     * @param originalFileName     The name of the resource to move.
     * @return An ExistQuery that moves and possibly renames a resource in the datbase.
     */
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

    /**
     * @param id The charter to publish.
     * @return An ExistQuery that publishes a saved charter. It overwrites the original charter in the process.
     */
    @NotNull
    public static ExistQuery publishCharter(@NotNull IdCharter id) {

        String publishedParentUri = Charter.createParentUri(id, CharterStatus.PUBLIC, null);
        String savedFileName = Charter.createResourceName(id, CharterStatus.SAVED);
        String publishedFileName = Charter.createResourceName(id, CharterStatus.PUBLIC);

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
     * The executed database returns <code>true</code> if the deletion was successful.
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
     * The executed database returns <code>true</code> if the deletion was successful.
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

    /**
     * @param existResource The resource to store.
     * @return An ExistQuery that stores a resource in the database. When executed, the database returns the URI of
     * the new resource if successful.
     */
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

    /**
     * @param parentUri       The parent URI of the charter resource.
     * @param oldAtomId       The old id.
     * @param newAtomId       The new id.
     * @param newResourceName The name of the resource after the change.
     * @return An ExistQuery that changes the <code>atom:id</code> of a charter. It renames the charter
     * resource in the process.
     */
    @NotNull
    public static ExistQuery updateCharterAtomId(@NotNull String parentUri, @NotNull String oldAtomId,
                                                 @NotNull String newAtomId, @NotNull String newResourceName) {

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
                newResourceName);

        return new ExistQuery(query);

    }

    /**
     * @param charter The charter to update.
     * @return An ExistQuery that updates the CEI-content of an existing charter. The ATOM-content will <b>not</b> be
     * updated apart from <code>atom:updated</code>. The executed database returns <code>0</code> if atom:updated after the updates
     * equals the current time at the update, meaning the update was successful), otherwise<code>1</code>
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
