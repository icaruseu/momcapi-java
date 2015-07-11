package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.resource.ResourceRoot;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.atom.AtomIdCharter;
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
     * Query charter existence.
     *
     * @param charterId    the charter's {@code atom:id}
     * @param resourceRoot the
     * @return the exist query
     */
    @NotNull
    public static ExistQuery queryCharterExistence(@NotNull AtomIdCharter charterId, @Nullable ResourceRoot resourceRoot) {

        return new ExistQuery(String.format(
                "%s collection('/db/mom-data%s')//atom:entry[.//atom:id/text()='%s'][1]",
                getNamespaceDeclaration(Namespace.ATOM),
                (resourceRoot == null) ? "" : ("/" + resourceRoot.getValue()),
                charterId.getAtomId()));

    }

    /**
     * Query charter uris.
     *
     * @param charterId the charter id
     * @return the exist query
     */
    @NotNull
    public static ExistQuery queryCharterUris(@NotNull AtomIdCharter charterId) {

        return new ExistQuery(String.format(
                "%s let $nodes := collection('/db/mom-data')//atom:entry[.//atom:id/text()='%s']" +
                        " for $node in $nodes" +
                        " return concat(util:collection-name($node), '/', util:document-name($node))",
                getNamespaceDeclaration(Namespace.ATOM),
                charterId.getAtomId()));

    }

    /**
     * Replace first occurrence in resource.
     *
     * @param resourceUri          the resource uri
     * @param qualifiedElementName the qualified element name
     * @param newElement           the new element
     * @return the exist query
     */
    @NotNull
    public static ExistQuery replaceFirstOccurrenceInResource(@NotNull String resourceUri, @NotNull String qualifiedElementName, @NotNull String newElement) {

        return new ExistQuery(String.format("%s update replace doc('%s')//%s[1] with %s",
                getNamespaceDeclaration(qualifiedElementName),
                resourceUri,
                qualifiedElementName,
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

}
