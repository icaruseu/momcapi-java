package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 09/08/2015.
 */
abstract class AbstractManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractManager.class);

    @NotNull
    final MomcaConnection momcaConnection;

    AbstractManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    @NotNull
    Optional<ExistResource> getExistResource(@NotNull String resourceUri) {

        LOGGER.debug("Trying to get resource with uri '{}' from the database.", resourceUri);

        String resourceName = Util.getLastUriPart(resourceUri);
        String parentUri = Util.getParentUri(resourceUri);

        Optional<ExistResource> resource = momcaConnection.readExistResource(resourceName, parentUri);

        LOGGER.debug("Returning '{}' for URI '{}' from the database.", resource, resourceUri);

        return resource;

    }

    @NotNull
    Optional<ExistResource> getFirstMatchingExistResource(@NotNull AtomId atomId) {

        String atomIdText = atomId.getText();

        LOGGER.debug("Trying to get eXist resource with atom:id '{}' from the database.", atomIdText);

        List<String> resourceUris = momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(atomId, null))
                .stream()
                .filter(s -> !s.contains("ead.old.")) // excluding old ead files
                .collect(Collectors.toList());

        Optional<ExistResource> resource = Optional.empty();

        if (!resourceUris.isEmpty()) {

            if (resourceUris.size() > 1) {
                LOGGER.debug("More than one result for atomId '{}, using first result, '{}'.'",
                        atomIdText, resourceUris.get(0));
            }

            resource = getExistResource(resourceUris.get(0));

        }

        LOGGER.debug("Returning '{}' for atom:id '{}' from the database.", resource, atomIdText);

        return resource;

    }

}
