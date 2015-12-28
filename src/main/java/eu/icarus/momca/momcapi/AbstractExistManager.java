package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 09/08/2015.
 */
abstract class AbstractExistManager {

    @NotNull
    final ExistMomcaConnection momcaConnection;

    AbstractExistManager(@NotNull ExistMomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

}
