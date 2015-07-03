package eu.icarus.momca.momcapi.exist;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
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
    public String queryUserModerator(@NotNull String userName) {

        return String.format(
                "declare namespace xrx='http://www.monasterium.net/NS/xrx';" +
                        " collection('/db/mom-data/xrx.user')/xrx:user[.//xrx:email='%s']/xrx:moderator/text()",
                userName);

    }

}
