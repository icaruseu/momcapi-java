package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;

/**
 * The abstract manager for managers that us an eXist MOM-CA connection.
 */
@SuppressWarnings("ClassWithoutLogger")
abstract class AbstractExistManager {

    @NotNull
    final ExistMomcaConnection momcaConnection;

    AbstractExistManager(@NotNull ExistMomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

}
