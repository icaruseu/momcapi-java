package eu.icarus.momca.momcapi.exist;

import eu.icarus.momca.momcapi.resource.Namespace;
import eu.icarus.momca.momcapi.resource.atom.AtomIdCharter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 07.03.2015.
 */
public class ExistQueryFactory {

    public ExistQueryFactory() {
    }

    @NotNull
    public String queryCharterExistence(@NotNull AtomIdCharter charterId, @Nullable MetadataCollectionName metadataCollectionName) {

        return String.format(
                "%s collection('/db/mom-data%s')//atom:entry[.//atom:id/text()='%s'][1]",
                getNamespaceDeclaration(Namespace.ATOM),
                (metadataCollectionName == null) ? "" : ("/" + metadataCollectionName.getValue()),
                charterId.getAtomId());

    }

    @NotNull
    public String queryCharterUris(@NotNull AtomIdCharter charterId) {

        return String.format(
                "%s let $nodes := collection('/db/mom-data')//atom:entry[.//atom:id/text()='%s']" +
                        " for $node in $nodes" +
                        " return concat(util:collection-name($node), '/', util:document-name($node))",
                getNamespaceDeclaration(Namespace.ATOM),
                charterId.getAtomId());

    }

    @NotNull
    public String replaceFirstOccurrenceInResource(@NotNull String resourceUri, @NotNull String qualifiedElementName, @NotNull String newElement) {

        return String.format("%s update replace doc('%s')//%s[1] with %s",
                getNamespaceDeclaration(qualifiedElementName),
                resourceUri,
                qualifiedElementName,
                newElement);

    }

    @NotNull
    private String getNamespaceDeclaration(@NotNull Namespace... namespaces) {

        StringBuilder declarationBuilder = new StringBuilder();

        for (Namespace namespace : namespaces) {
            declarationBuilder.append(String.format("declare namespace %s='%s';", namespace.getPrefix(),
                    namespace.getUri()));
        }

        return declarationBuilder.toString();

    }

    @NotNull
    private String getNamespaceDeclaration(@NotNull String... qualifiedElementNames) {

        List<Namespace> namespaceList = new ArrayList<>(0);

        for (String element : qualifiedElementNames) {
            String namespaceString = element.substring(0, element.indexOf(':')).toUpperCase();
            namespaceList.add(Namespace.valueOf(namespaceString));
        }

        return getNamespaceDeclaration(namespaceList.toArray(new Namespace[namespaceList.size()]));

    }

}
