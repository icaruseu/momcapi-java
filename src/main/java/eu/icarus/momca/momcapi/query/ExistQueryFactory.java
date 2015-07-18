package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.resource.ResourceRoot;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.atom.Id;
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

    @NotNull
    public static ExistQuery appendElement(@NotNull String resourceUri, @NotNull String qualifiedParentName,
                                           @NotNull String elementToInsert) {

        String query = String.format(
                "%s update insert %s into doc('%s')//%s",
                getNamespaceDeclaration(qualifiedParentName),
                elementToInsert,
                resourceUri,
                qualifiedParentName);

        return new ExistQuery(query);

    }

    /**
     * @param resourceId   The resource's {@code atom:id}.
     * @param resourceRoot The resource root of the resource the search should be restricted to. If {@code null},
     *                     the whole database is searched.
     * @return A query to check if a resource matching {@code resourceId} is existing in the database at the
     * specified {@code resourceRoot}.
     */
    @NotNull
    public static ExistQuery checkResourceExistence(@NotNull Id resourceId, @Nullable ResourceRoot resourceRoot) {

        String query = String.format(
                "%s collection('/db/mom-data%s')//atom:entry[.//atom:id/text()='%s'][1]",
                getNamespaceDeclaration(Namespace.ATOM),
                getRootCollectionString(resourceRoot),
                resourceId.getAtomId());

        return new ExistQuery(query);

    }

    /**
     * @param code The code of the country.
     * @return A query to get the complete XML content of the {@code eap:country}-element specified by {@code code}.
     */
    @NotNull
    public static ExistQuery getCountryXml(@NotNull String code) {

        String query = String.format(
                "%s doc('/db/mom-data/%s/mom.portal.xml')//eap:country[eap:code='%s']",
                getNamespaceDeclaration(Namespace.EAP),
                ResourceRoot.METADATA_PORTAL_PUBLIC.getCollectionName(),
                code);

        return new ExistQuery(query);

    }

    /**
     * @param resourceId   The {@code atom:id} of the resource to locate.
     * @param resourceRoot The resource root of the resource the search should be restricted to. If {@code null},
     *                     the whole database is searched.
     * @return A query to get the absolute URIs, e.g. {@code /db/mom-data/xrx.user/admin.xml}, of all resources
     * matching {@code resourceId} in {@code ResourceRoot} in the database.
     */
    @NotNull
    public static ExistQuery getResourceUri(@NotNull Id resourceId, @Nullable ResourceRoot resourceRoot) {

        String query = String.format(
                "%s let $nodes := collection('/db/mom-data%s')//atom:entry[.//atom:id/text()='%s']" +
                        " for $node in $nodes" +
                        " return concat(util:collection-name($node), '/', util:document-name($node))",
                getNamespaceDeclaration(Namespace.ATOM),
                getRootCollectionString(resourceRoot),
                resourceId.getAtomId());

        return new ExistQuery(query);

    }

    /**
     * @return A query to get a list of the text content of all {@code eap:country/eap:code} elements. This is
     * effectively a list of all countries registered in the portal.
     */
    @NotNull
    public static ExistQuery listCountryCodes() {

        String query = String.format(
                "%s doc('/db/mom-data/%s/mom.portal.xml')//eap:country/eap:code/text()",
                getNamespaceDeclaration(Namespace.EAP),
                ResourceRoot.METADATA_PORTAL_PUBLIC.getCollectionName());

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
        return (resourceRoot == null) ? "" : ("/" + resourceRoot.getCollectionName());
    }

}
