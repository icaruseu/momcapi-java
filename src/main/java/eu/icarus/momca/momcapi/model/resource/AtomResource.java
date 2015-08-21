package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.id.IdAtomId;
import eu.icarus.momca.momcapi.model.id.IdUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by djell on 21/08/2015.
 */
public abstract class AtomResource extends ExistResource {

    @NotNull
    Optional<IdUser> creator = Optional.empty();
    @NotNull
    IdAtomId id;
    @NotNull
    String name;

    AtomResource(@NotNull String identifier, @NotNull String name, @NotNull ResourceType resourceType) {

        super(new ExistResource(
                identifier + resourceType.getNameSuffix(),
                ResourceRoot.ARCHIVES.getUri() + "/" + identifier,
                "<empty/>"));

        setIdentifier(identifier);
        setName(name);

    }

    AtomResource(@NotNull ExistResource existResource) {

        super(existResource);

        Optional<String> nameOptional = getNameFromXml(existResource);
        Optional<String> identifierOptional = getIdentifierFromXml(existResource);
        if (!identifierOptional.isPresent() || !nameOptional.isPresent()) {
            throw new IllegalArgumentException("The provided resource content is not a valid ExistResource: "
                    + existResource.toDocument().toXML());
        }

        setIdentifier(identifierOptional.get());
        setName(nameOptional.get());

    }

    @NotNull
    public final Optional<IdUser> getCreator() {
        return creator;
    }

    public final void setCreator(@Nullable String creator) {

        if (creator == null || creator.isEmpty()) {
            this.creator = Optional.empty();
        } else {
            this.creator = Optional.of(new IdUser(creator));
        }

    }

    @NotNull
    public abstract IdAtomId getId();

    @NotNull
    public final String getIdentifier() {
        return id.getIdentifier();
    }

    public abstract void setIdentifier(@NotNull String identifier);

    @NotNull
    abstract Optional<String> getIdentifierFromXml(ExistResource existResource);

    @NotNull
    public final String getName() {
        return name;
    }

    public final void setName(@NotNull String name) {

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

    }

    @NotNull
    abstract Optional<String> getNameFromXml(ExistResource existResource);

}
