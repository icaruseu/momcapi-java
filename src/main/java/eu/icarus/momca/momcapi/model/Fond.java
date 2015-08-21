package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Created by daniel on 17.07.2015.
 */
public class Fond extends ExistResource {

    @NotNull
    private final Optional<URL> dummyImageUrl;
    @NotNull
    private final Optional<ExistResource> fondPreferences;
    @NotNull
    private final IdFond id;
    @NotNull
    private final Optional<ImageAccess> imageAccess;
    @NotNull
    private final Optional<URL> imagesUrl;
    @NotNull
    private final String name;

    public Fond(@NotNull ExistResource fondResource, @NotNull Optional<ExistResource> fondPreferences) {

        super(fondResource);

        id = initId();
        this.fondPreferences = fondPreferences;
        this.name = queryUniqueElement(XpathQuery.QUERY_EAD_UNITTITLE);
        this.imageAccess = initImageAccess();
        this.dummyImageUrl = initDummyImageUrl();
        this.imagesUrl = initImagesUrl();

    }

    @NotNull
    private Optional<URL> createUrl(@NotNull String urlString) {

        Optional<URL> url = Optional.empty();

        try {

            if (!urlString.isEmpty()) {
                url = Optional.of(new URL(urlString));
            }

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(urlString + " is not a valid URL.");
        }

        return url;

    }

    @NotNull
    public IdArchive getArchiveId() {
        return id.getIdArchive();
    }

    @NotNull
    public Optional<URL> getDummyImageUrl() {
        return dummyImageUrl;
    }

    @NotNull
    public IdFond getId() {
        return id;
    }

    @NotNull
    public String getIdentifier() {
        return id.getIdentifier();
    }

    @NotNull
    public Optional<ImageAccess> getImageAccess() {
        return imageAccess;
    }

    @NotNull
    public Optional<URL> getImagesUrl() {
        return imagesUrl;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    private Optional<URL> initDummyImageUrl() {

        Optional<URL> url = Optional.empty();

        if (fondPreferences.isPresent()) {
            String urlString = fondPreferences.get().queryUniqueElement(XpathQuery.QUERY_XRX_DUMMY_IMAGE_URL);
            url = createUrl(urlString);
        }

        return url;

    }

    @NotNull
    private IdFond initId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", toDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdFond(new AtomId(idString));
        }

    }

    @NotNull
    private Optional<ImageAccess> initImageAccess() {

        Optional<ImageAccess> access = Optional.empty();

        if (fondPreferences.isPresent()) {

            String imageAccessString = fondPreferences.get().queryUniqueElement(XpathQuery.QUERY_XRX_IMAGE_ACCESS);
            access = Optional.of(ImageAccess.fromText(imageAccessString));

        }

        return access;
    }

    @NotNull
    private Optional<URL> initImagesUrl() {

        Optional<URL> url = Optional.empty();

        if (fondPreferences.isPresent()) {
            String urlString = fondPreferences.get().queryUniqueElement(XpathQuery.QUERY_XRX_IMAGE_SERVER_BASE_URL);
            url = createUrl(urlString);
        }

        return url;

    }

    @NotNull
    @Override
    public String toString() {
        return "Fond{" +
                "id=" + id +
                ", dummyImageUrl=" + dummyImageUrl +
                ", fondPreferences=" + fondPreferences +
                ", imageAccess=" + imageAccess +
                ", imagesUrl=" + imagesUrl +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }

}
