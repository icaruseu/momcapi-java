package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.MomcaResource;
import eu.icarus.momca.momcapi.xml.atom.Id;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 09/08/2015.
 */
abstract class AbstractManager {

    @NotNull
    final MomcaConnection momcaConnection;

    public AbstractManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    @NotNull
    Optional<MomcaResource> getMomcaResource(@NotNull Id id) {

        List<String> resourceUris = momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(id, null));

        Optional<MomcaResource> resource = Optional.empty();

        if (!resourceUris.isEmpty()) {

            if (resourceUris.size() > 1) {
                String message = String.format("More than one result for id '%s'", id.getId());
                throw new MomcaException(message);
            }

            resource = getMomcaResource(resourceUris.get(0));

        }

        return resource;

    }

    @NotNull
    Optional<MomcaResource> getMomcaResource(@NotNull String resourceUri) {
        String resourceName = Util.getLastUriPart(resourceUri);
        String parentUri = Util.getParentUri(resourceUri);
        return momcaConnection.getExistResource(resourceName, parentUri);
    }

}