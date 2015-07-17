package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.resource.ResourceRoot;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
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
     * @param resourceId   The resource's {@code atom:id}.
     * @param resourceRoot The resource root of the resource the search should be restricted to. If {@code null},
     *                     the whole database is searched.
     * @return A query to check if a resource matching {@code resourceId} is existing in the database at the
     * specified {@code resourceRoot}.
     */
    @NotNull
    public static ExistQuery checkResourceExistence(@NotNull AtomId resourceId, @Nullable ResourceRoot resourceRoot) {

        return new ExistQuery(String.format(
                "%s collection('/db/mom-data%s')//atom:entry[.//atom:id/text()='%s'][1]",
                getNamespaceDeclaration(Namespace.ATOM),
                getRootCollectionString(resourceRoot),
                resourceId.getAtomId()));

    }

    /**
     * @param resourceId   The {@code atom:id} of the resource to locate.
     * @param resourceRoot The resource root of the resource the search should be restricted to. If {@code null},
     *                     the whole database is searched.
     * @return A query to get the absolute URIs, e.g. {@code /db/mom-data/xrx.user/admin.xml}, of all resources
     * matching {@code resourceId} in {@code ResourceRoot} in the database.
     */
    @NotNull
    public static ExistQuery getResourceUri(@NotNull AtomId resourceId, @Nullable ResourceRoot resourceRoot) {

        return new ExistQuery(String.format(
                "%s let $nodes := collection('/db/mom-data%s')//atom:entry[.//atom:id/text()='%s']" +
                        " for $node in $nodes" +
                        " return concat(util:collection-name($node), '/', util:document-name($node))",
                getNamespaceDeclaration(Namespace.ATOM),
                getRootCollectionString(resourceRoot),
                resourceId.getAtomId()));

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

        return new ExistQuery(String.format("%s update replace doc('%s')//%s[1] with %s",
                getNamespaceDeclaration(elementToReplace),
                resourceUri,
                elementToReplace,
                newElement));

    }

    @NotNull
    private static String getNamespaceDeclaration(@NotNull Namespace... namespaces) {

        StringBuilder declarationBuilder = new StringBuilder();

        for (Namespace namespace : namespaces) {
            declarationBuilder.append(String.format("declare namespace %s='%s';", namespace.getPrefix(),
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
