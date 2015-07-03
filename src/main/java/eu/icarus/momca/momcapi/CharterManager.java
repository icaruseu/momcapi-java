package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import eu.icarus.momca.momcapi.exception.MomCAException;
import eu.icarus.momca.momcapi.exist.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Charter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by daniel on 03.07.2015.
 */
public class CharterManager {

    @NotNull
    private static final ExistQueryFactory QUERY_FACTORY = new ExistQueryFactory();

    private final MomcaConnection momcaConnection;

    CharterManager(MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    @NotNull
    public List<Charter> getCharterInstances(@NotNull CharterAtomId charterAtomId) throws MomCAException {

        List<Charter> charters = new ArrayList<>(0);
        for (String charterUri : momcaConnection.queryDatabase(QUERY_FACTORY.queryCharterUris(charterAtomId))) {
            getCharterFromUri(charterUri).ifPresent(charters::add);
        }
        return charters;

    }

    @NotNull
    public List<CharterAtomId> listErroneouslySavedCharters(@NotNull String userName) throws MomCAException {

        UserManager um = new UserManager(momcaConnection);

        List<CharterAtomId> erroneouslySavedCharters = new ArrayList<>(0);
        um.getUser(userName).ifPresent(user -> erroneouslySavedCharters.addAll(user.listSavedCharterIds()));

        for (CharterAtomId id : erroneouslySavedCharters) {
            if (isCharterExisting(id)) {
                erroneouslySavedCharters.remove(id);
            }
        }

        return erroneouslySavedCharters;

    }

    @NotNull
    private Optional<Charter> getCharterFromUri(@NotNull String charterUri) throws MomCAException {
        String resourceName = charterUri.substring(charterUri.lastIndexOf('/') + 1, charterUri.length());
        String parentUri = charterUri.substring(0, charterUri.lastIndexOf('/'));
        return momcaConnection.getExistResource(resourceName, parentUri).map(Charter::new);
    }

    private boolean isCharterExisting(@NotNull CharterAtomId charterAtomId) throws MomCAException {
        return !momcaConnection.queryDatabase(QUERY_FACTORY.queryCharterExistence(charterAtomId)).isEmpty();
    }

}
