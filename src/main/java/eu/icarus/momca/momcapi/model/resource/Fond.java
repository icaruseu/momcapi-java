package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.ImageAccess;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.model.xml.ead.*;
import eu.icarus.momca.momcapi.query.XpathQuery;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by daniel on 17.07.2015.
 */
public class Fond extends AtomResource {

    @NotNull
    private Bibliography bibliography = new Bibliography();
    @NotNull
    private BiogHist biogHist = new BiogHist();
    @NotNull
    private CustodHist custodHist = new CustodHist();
    @NotNull
    private Optional<URL> dummyImageUrl = Optional.empty();
    @NotNull
    private Optional<ExistResource> fondPreferences = Optional.empty();
    @NotNull
    private Optional<ImageAccess> imageAccess = Optional.empty();
    @NotNull
    private Optional<URL> imagesUrl = Optional.empty();
    @NotNull
    private String name;
    @NotNull
    private List<Odd> oddList = new ArrayList<>(0);


    public Fond(@NotNull String identifier, @NotNull IdArchive parentArchive, @NotNull String name) {

        super(new IdFond(identifier, parentArchive.getIdentifier()), ResourceType.FOND, ResourceRoot.ARCHIVAL_FONDS);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

        oddList.add(new Odd());

        updateXmlContent();

    }

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
        return getId().getIdArchive();
    }

    @NotNull
    public Optional<URL> getDummyImageUrl() {
        return dummyImageUrl;
    }

    @NotNull
    public IdFond getId() {
        return (IdFond) id;
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

    @Override
    public void setIdentifier(@NotNull String identifier) {

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

    @Override
    void updateXmlContent() {

        Did didElement = new Did(id.getIdentifier(), name);


    }

}
