package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.id.IdAtomId;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.query.XpathQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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

        List<String> identifierList = existResource.queryContentAsList(XpathQuery.QUERY_EAG_REPOSITORID);
        List<String> nameList = existResource.queryContentAsList(XpathQuery.QUERY_EAG_AUTFORM);

        if (identifierList.size() != 1 || nameList.size() != 1) {
            throw new IllegalArgumentException("The provided resource content is not a valid ExistResource: "
                    + existResource.toDocument().toXML());
        }

        setIdentifier(identifierList.get(0));
        setName(nameList.get(0));


    }

    @NotNull
    public Optional<IdUser> getCreator() {
        return creator;
    }

    public void setCreator(@Nullable String creator) {

        if (creator == null || creator.isEmpty()) {
            this.creator = Optional.empty();
        } else {
            this.creator = Optional.of(new IdUser(creator));
        }

    }

    @NotNull
    public abstract IdAtomId getId();

    @NotNull
    public String getIdentifier() {
        return id.getIdentifier();
    }

    public abstract void setIdentifier(@NotNull String identifier);

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

    }

}
