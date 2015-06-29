package eu.icarus.momca.momcapi.existquery;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import org.jetbrains.annotations.NotNull;
import org.xmldb.api.modules.XMLResource;

/**
 * Created by Daniel on 07.03.2015.
 */
public final class ExistQueryFactory {

    @NotNull
    public ExistQuery getResourceContent(@NotNull XMLResource resource) {
        String query = String.format("doc('%s')", resource);
        return new ExistQuery(query);
    }

    @NotNull
    public ExistQuery queryUrisOfImportedCharter(@NotNull CharterAtomId charterId) {

        String query;

        if (charterId.isPartOfArchiveFond()) {

            query = String.format(
                    "declare namespace atom = 'http://www.w3.org/2005/Atom';" +
                            " declare namespace cei = 'http://www.monasterium.net/NS/cei';" +
                            " let $nodes := (collection('/db/mom-data/metadata.charter.import/%s/%s')//atom:entry[.//cei:idno/@id='%s'])" +
                            " for $node in $nodes" +
                            " return concat(util:collection-name($node), '/', util:document-name($node))",
                    charterId.getArchiveId().get(),
                    charterId.getFondId().get(),
                    charterId.getCharterId());
        } else {
            query = String.format(
                    "declare namespace atom = 'http://www.w3.org/2005/Atom';" +
                            " declare namespace cei = 'http://www.monasterium.net/NS/cei';" +
                            " let $nodes := (collection('/db/mom-data/metadata.charter.import/%s')//atom:entry[.//cei:idno/@id='%s'])" +
                            " for $node in $nodes" +
                            " return concat(util:collection-name($node), '/', util:document-name($node))",
                    charterId.getCollectionId().get(),
                    charterId.getCharterId());
        }

        return new ExistQuery(query);

    }

    @NotNull
    public ExistQuery queryUserModerator(@NotNull String userName) {

        String query = String.format(
                "declare namespace xrx='http://www.monasterium.net/NS/xrx';" +
                        " collection('/db/mom-data/xrx.user')/xrx:user[.//xrx:email='%s']/xrx:moderator/text()",
                userName);
        return new ExistQuery(query);

    }

}
