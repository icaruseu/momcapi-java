package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.id.IdAtomId;
import eu.icarus.momca.momcapi.model.id.IdCharter;
import eu.icarus.momca.momcapi.model.id.IdMyCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.Charter;
import eu.icarus.momca.momcapi.model.resource.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 28/12/2015.
 */
public interface CharterManager {

    boolean addCharter(@NotNull Charter charter);

    boolean deletePrivateCharter(@NotNull IdCharter id, @NotNull IdUser creator);

    boolean deletePublicCharter(@NotNull IdCharter id, @NotNull CharterStatus status);

    @NotNull
    Optional<Charter> getCharter(@NotNull IdCharter idCharter, @NotNull CharterStatus charterStatus);

    @NotNull
    List<Charter> getCharterInstances(@NotNull IdCharter idCharter);

    boolean isExisting(@NotNull IdCharter idCharter, @Nullable CharterStatus status);

    @NotNull
    List<IdCharter> listChartersInPrivateMyCollection(@NotNull IdMyCollection idMyCollection);

    @NotNull
    List<IdCharter> listImportedCharters(@NotNull IdAtomId idParent);

    /**
     * Lists charters that are listed in a users file without them being properly saved to metadata.charter.saved.
     * This is due to an old bug in the database.
     *
     * @param idUser the User
     * @return A list of improperly saved charters.
     */
    @NotNull
    List<IdCharter> listNotExistingSavedCharters(@NotNull IdUser idUser);

    @NotNull
    List<IdCharter> listPublicCharters(@NotNull IdAtomId idParent);

    @NotNull
    List<IdCharter> listSavedCharters();

    @NotNull
    List<IdCharter> listUsersPrivateCharters(@NotNull IdUser idUser);

    boolean publishSavedCharter(@NotNull User user, @NotNull IdCharter idCharter);

    /**
     * Update the CEI content of a charter. The ATOM content, apart from @code{atom:updated} will not be updated
     * (for example @code{atom:author}).
     *
     * @param updatedCharter The charter with updated content.
     * @return true if the update was successful.
     */
    boolean updateCharterContent(@NotNull Charter updatedCharter);

    boolean updateCharterId(@NotNull IdCharter newId, @NotNull IdCharter originalId,
                            @NotNull CharterStatus status, @Nullable IdUser creator);

    boolean updateCharterStatus(@NotNull CharterStatus newStatus, @NotNull CharterStatus originalStatus,
                                @NotNull IdCharter idCharter, @Nullable IdUser creator);

}
