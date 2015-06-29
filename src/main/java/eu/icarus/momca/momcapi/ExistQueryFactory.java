package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Daniel on 07.03.2015.
 */
public class ExistQueryFactory {

    public ExistQueryFactory() {
    }

    @NotNull
    public String queryUrisOfCharter(@NotNull String path, String charterId) {

        return String.format(
                "declare namespace atom = 'http://www.w3.org/2005/Atom';" +
                        " declare namespace cei = 'http://www.monasterium.net/NS/cei';" +
                        " let $nodes := (collection('%s')//atom:entry[.//cei:idno/@id='%s'])" +
                        " for $node in $nodes" +
                        " return concat(util:collection-name($node), '/', util:document-name($node))",
                path,
                charterId);
    }

    @NotNull
    public String queryUserModerator(@NotNull String userName) {

        return String.format(
                "declare namespace xrx='http://www.monasterium.net/NS/xrx';" +
                        " collection('/db/mom-data/xrx.user')/xrx:user[.//xrx:email='%s']/xrx:moderator/text()",
                userName);

    }

}
