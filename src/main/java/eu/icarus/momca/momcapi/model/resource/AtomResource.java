package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.id.IdAtomId;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.xml.atom.AtomAuthor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    AtomResource(@NotNull IdAtomId id, @NotNull String name,
                 @NotNull ResourceType resourceType, @NotNull ResourceRoot resourceRoot) {

        super(new ExistResource(
                String.format("%s%s", id.getIdentifier(), resourceType.getNameSuffix()),
                String.format("%s/%s", resourceRoot.getUri(), id),
                "<empty/>"));

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.id = id;
        this.name = name;

    }

    AtomResource(@NotNull ExistResource existResource) {

        super(existResource);

        Optional<String> nameOptional = readNameFromXml(existResource);
        Optional<String> identifierOptional = readIdentifierFromXml(existResource);

        if (!identifierOptional.isPresent() || !nameOptional.isPresent()) {
            throw new IllegalArgumentException("The provided resource content is not a valid ExistResource: "
                    + existResource.toDocument().toXML());
        }

        setIdentifier(identifierOptional.get());
        setName(nameOptional.get());

    }

    @NotNull
    static String localTime() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @NotNull
    AtomAuthor createAtomAuthor() {
        return getCreator().map(IdUser::getContentXml).orElse(new AtomAuthor(""));
    }

    @NotNull
    public final Optional<IdUser> getCreator() {
        return creator;
    }

    @NotNull
    public abstract IdAtomId getId();

    @NotNull
    public final String getIdentifier() {
        return id.getIdentifier();
    }

    @NotNull
    public final String getName() {
        return name;
    }

    @NotNull
    abstract Optional<String> readIdentifierFromXml(ExistResource existResource);

    @NotNull
    abstract Optional<String> readNameFromXml(ExistResource existResource);

    public final void setCreator(@Nullable String creator) {

        if (creator == null || creator.isEmpty()) {
            this.creator = Optional.empty();
        } else {
            this.creator = Optional.of(new IdUser(creator));
        }

        updateXmlContent();

    }

    public abstract void setIdentifier(@NotNull String identifier);

    public final void setName(@NotNull String name) {

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

        updateXmlContent();

    }

    abstract void updateXmlContent();

}
