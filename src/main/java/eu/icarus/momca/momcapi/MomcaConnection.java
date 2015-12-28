package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 28/12/2015.
 */
public interface MomcaConnection {

    boolean closeConnection();

    @NotNull
    ArchiveManager getArchiveManager();

    @NotNull
    CharterManager getCharterManager();

    @NotNull
    CollectionManager getCollectionManager();

    @NotNull
    CountryManager getCountryManager();

    @NotNull
    FondManager getFondManager();

    @NotNull
    MyCollectionManager getMyCollectionManager();

    @NotNull
    ExistUserExistManager getUserManager();

}
