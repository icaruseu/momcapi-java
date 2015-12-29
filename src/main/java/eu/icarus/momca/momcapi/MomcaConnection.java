package eu.icarus.momca.momcapi;

import org.jetbrains.annotations.NotNull;

/**
 * The Connection to the MOM-CA database. Provides access to all managers.
 */
interface MomcaConnection {

    /**
     * Closes the connection to the database instance.
     *
     * @return <code>True</code> if the connection was successfully closed.
     */
    boolean closeConnection();

    /**
     * @return The archive manager. Provides actions related to management of archives.
     */
    @NotNull
    ArchiveManager getArchiveManager();

    /**
     * @return The charter manager. Provides actions related to management of charters.
     */
    @NotNull
    CharterManager getCharterManager();

    /**
     * @return The collection manager. Provides actions related to management of archival collections.
     */
    @NotNull
    CollectionManager getCollectionManager();

    /**
     * @return The country manager. Provides actions related to management of countries and regions.
     */
    @NotNull
    CountryManager getCountryManager();

    /**
     * @return The fond manager. Provides actions related to archival fonds.
     */
    @NotNull
    FondManager getFondManager();

    /**
     * @return The myCollection manager. Provides actions related to user collections.
     */
    @NotNull
    MyCollectionManager getMyCollectionManager();

    /**
     * @return The user manager. Provides actions related to users.
     */
    @NotNull
    ExistUserManager getUserManager();

}
