package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Address;
import eu.icarus.momca.momcapi.resource.Archive;
import eu.icarus.momca.momcapi.resource.ContactInformation;
import eu.icarus.momca.momcapi.resource.MomcaResource;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.atom.Author;
import eu.icarus.momca.momcapi.xml.atom.Entry;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.eag.Desc;
import eu.icarus.momca.momcapi.xml.eap.Country;
import eu.icarus.momca.momcapi.xml.eap.Subdivision;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by daniel on 20.07.2015.
 */
public class HierarchyManager {

    @NotNull
    private final MomcaConnection momcaConnection;

    public HierarchyManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    @NotNull
    public Archive addArchive(@NotNull String authorEmail, @NotNull String shortName, @NotNull String name, @NotNull Country country,
                              @Nullable Subdivision subdivision, @NotNull Address address,
                              @NotNull ContactInformation contactInformation, @NotNull String logoUrl) {

        IdArchive id = new IdArchive(shortName);

        if (getArchive(id).isPresent()) {
            String message = String.format("The archive '%s' that is to be added already exists.", id);
            throw new IllegalArgumentException(message);
        }

        if (!momcaConnection.getUserManager().getUser(authorEmail).isPresent()) {
            String message = String.format("The author '%s' is not existing in the database.", authorEmail);
            throw new IllegalArgumentException(message);
        }

        String archivesCollection = "/db/mom-data/metadata.archive.public";
        momcaConnection.addCollection(shortName, archivesCollection);

        String resourceName = shortName + ".eag.xml";
        String parentCollectionUri = archivesCollection + "/" + shortName;
        Element resourceContent = createNewArchiveResourceContent(authorEmail,
                shortName, name, country, subdivision, address, contactInformation, logoUrl);

        MomcaResource resource = new MomcaResource(resourceName,
                parentCollectionUri, resourceContent.toXML());

        momcaConnection.storeExistResource(resource);

        return getArchive(id).orElseThrow(RuntimeException::new);

    }

    public void deleteArchive(@NotNull Archive archive) {
        // TODO implement
    }

    @NotNull
    public Optional<Archive> getArchive(@NotNull IdArchive archiveId) {

        List<String> archiveUris = momcaConnection.queryDatabase(ExistQueryFactory.getResourceUri(archiveId, null));

        if (archiveUris.size() > 1) {
            String message = String.format("More than one result for archive '%s'", archiveId.getArchiveIdentifier());
            throw new MomcaException(message);
        }

        return archiveUris.isEmpty() ? Optional.empty() : getArchiveFromUri(archiveUris.get(0));

    }

    @NotNull
    public List<IdArchive> listArchives() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listIdArchives());
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdArchive> listArchivesForCountry(@NotNull Country country) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listIdArchivesForCountry(country.getCode()));
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdArchive> listArchivesForSubdivision(@NotNull Subdivision subdivision) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listIdArchivesForSubdivision(subdivision.getNativeform()));
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    private Element createEagElement(@NotNull String shortName, @NotNull String archiveName, @NotNull String countrycode, @NotNull Desc desc) {

        String eagUri = Namespace.EAG.getUri();

        Element eagEag = new Element("eag:eag", eagUri);

        Element eagArchguide = new Element("eag:archguide", eagUri);
        eagEag.appendChild(eagArchguide);

        Element eagIdentity = new Element("eag:identity", eagUri);
        eagArchguide.appendChild(eagIdentity);

        Element eagRepositorid = new Element("eag:repositorid", eagUri);
        eagRepositorid.addAttribute(new Attribute("countrycode", countrycode));
        eagRepositorid.appendChild(shortName);
        eagIdentity.appendChild(eagRepositorid);

        Element eagAutform = new Element("eag:autform", eagUri);
        eagIdentity.appendChild(eagAutform);
        eagAutform.appendChild(archiveName);

        eagArchguide.appendChild(desc);

        return eagEag;

    }

    @NotNull
    private Element createNewArchiveResourceContent(@NotNull String authorEmail, @NotNull String shortName,
                                                    @NotNull String name, @NotNull Country country,
                                                    @Nullable Subdivision subdivision, @NotNull Address address,
                                                    @NotNull ContactInformation contactInformation, @NotNull String logoUrl) {

        IdArchive id = new IdArchive(shortName);
        Author author = new Author(authorEmail);
        String now = momcaConnection.queryDatabase(ExistQueryFactory.getCurrentDateTime()).get(0);

        String subdivisionNativeform = subdivision == null ? "" : subdivision.getNativeform();
        Desc desc = new Desc(country.getNativeform(), subdivisionNativeform, address, contactInformation, logoUrl);
        Element eag = createEagElement(shortName, name, country.getCode(), desc);

        return new Entry(id, author, now, eag);

    }

    @NotNull
    private Optional<Archive> getArchiveFromUri(@NotNull String archiveUri) {
        String resourceName = Util.getLastUriPart(archiveUri);
        String parentUri = Util.getParentUri(archiveUri);
        return momcaConnection.getExistResource(resourceName, parentUri).map(Archive::new);
    }

}
