package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 09/08/2015.
 */
abstract class AbstractManager {

    @NotNull
    final MomcaConnection momcaConnection;

    AbstractManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    @NotNull
    Optional<ExistResource> getExistResource(@NotNull AtomId atomId) {

        List<String> resourceUris = momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(atomId, null));

        Optional<ExistResource> resource = Optional.empty();

        if (!resourceUris.isEmpty()) {

            if (resourceUris.size() > 1) {
                String message = String.format("More than one result for atomId '%s'", atomId.getText());
                throw new MomcaException(message);
            }

            resource = getExistResource(resourceUris.get(0));

        }

        return resource;

    }

    @NotNull
    Optional<ExistResource> getExistResource(@NotNull String resourceUri) {
        String resourceName = Util.getLastUriPart(resourceUri);
        String parentUri = Util.getParentUri(resourceUri);
        return momcaConnection.getExistResource(resourceName, parentUri);
    }

}
