package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 20.07.2015.
 */
public class HierarchyManager {

    @NotNull
    private final MomcaConnection momcaConnection;

    public HierarchyManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }


}
