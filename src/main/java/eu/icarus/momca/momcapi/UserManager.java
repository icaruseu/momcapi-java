package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 28/12/2015.
 */
public interface UserManager {

    boolean add(@NotNull User user, @NotNull String password);

    boolean changeModerator(@NotNull IdUser idUser, @NotNull IdUser idModerator);

    boolean changeUserPassword(@NotNull User user, @NotNull String newPassword);

    boolean delete(@NotNull IdUser idUser);

    @NotNull
    Optional<User> get(@NotNull IdUser idUser);

    boolean initialize(@NotNull IdUser idUser, @NotNull String password);

    boolean isExisting(@NotNull IdUser idUser);

    @NotNull
    List<IdUser> list();

}
