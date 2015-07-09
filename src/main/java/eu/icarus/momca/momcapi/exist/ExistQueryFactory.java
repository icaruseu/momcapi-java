package eu.icarus.momca.momcapi.exist;

import eu.icarus.momca.momcapi.resource.Namespace;
import eu.icarus.momca.momcapi.resource.atom.CharterAtomId;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Daniel on 07.03.2015.
 */
public class ExistQueryFactory {

    public ExistQueryFactory() {
    }

    @NotNull
    public String queryCharterExistence(@NotNull CharterAtomId charterId) {

        return String.format(
                "declare namespace atom = 'http://www.w3.org/2005/Atom';" +
                        " collection('/db/mom-data')//atom:entry[.//atom:id/text()='%s'][1]",
                charterId.getAtomId());

    }

    @NotNull
    public String queryCharterUris(@NotNull CharterAtomId charterId) {

        return String.format(
                "declare namespace atom = 'http://www.w3.org/2005/Atom';" +
                        " let $nodes := collection('/db/mom-data')//atom:entry[.//atom:id/text()='%s']" +
                        " for $node in $nodes" +
                        " return concat(util:collection-name($node), '/', util:document-name($node))",
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
    private String getNamespaceDeclaration(@NotNull String... qualifiedElementNames) {

        StringBuilder declarationBuilder = new StringBuilder();

        for (String element : qualifiedElementNames) {

            String namespaceString = element.substring(0, element.indexOf(':')).toUpperCase();
            Namespace namespace = Namespace.valueOf(namespaceString);
            declarationBuilder.append(String.format("declare namespace %s='%s';", namespace.getPrefix(),
                    namespace.getUri()));
        }
        return declarationBuilder.toString();

    }

}
