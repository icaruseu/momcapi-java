package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.*;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.xml.atom.Entry;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.eag.Desc;
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
public class ArchiveManager extends AbstractManager {

    public ArchiveManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @NotNull
    public Archive addArchive(@NotNull String authorEmail, @NotNull String identifier, @NotNull String name,
                              @NotNull Country country, @Nullable Region region, @NotNull Address address,
                              @NotNull ContactInformation contactInformation, @NotNull String logoUrl) {

        IdArchive id = new IdArchive(identifier);

        if (getArchive(id).isPresent()) {
            String message = String.format("The archive '%s' that is to be added already exists.", id);
            throw new IllegalArgumentException(message);
        }

        String archivesCollection = ResourceRoot.ARCHIVES.getUri();
        momcaConnection.addCollection(identifier, archivesCollection);

        String resourceName = identifier + ".eag.xml";
        String parentCollectionUri = archivesCollection + "/" + identifier;
        Element resourceContent = createNewArchiveResourceContent(authorEmail,
                identifier, name, country, region, address, contactInformation, logoUrl);

        MomcaResource resource = new MomcaResource(resourceName,
                parentCollectionUri, resourceContent.toXML());

        momcaConnection.storeExistResource(resource);

        return getArchive(id).orElseThrow(RuntimeException::new);

    }

    public void deleteArchive(@NotNull Archive archive) {

        if (!momcaConnection.getFondManager().listFonds(archive.getId()).isEmpty()) {
            String message = String.format("The archive '%s',  that is to be deleted still has associated fonds.",
                    archive.getIdentifier());
            throw new IllegalArgumentException(message);
        }

        momcaConnection.deleteCollection(String.format("%s/%s",
                ResourceRoot.ARCHIVES.getUri(), archive.getId().getArchiveIdentifier()));
        momcaConnection.deleteCollection(String.format("%s/%s",
                ResourceRoot.ARCHIVAL_FONDS.getUri(), archive.getId().getArchiveIdentifier()));

    }

    @NotNull
    public Optional<Archive> getArchive(@NotNull IdArchive idArchive) {
        return getMomcaResource(idArchive).map(Archive::new);
    }


    @NotNull
    public List<IdArchive> listArchives() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listArchives());
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdArchive> listArchives(@NotNull Country country) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listArchivesForCountry(country.getCountryCode()));
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdArchive> listArchives(@NotNull Region region) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listArchivesForRegion(region.getNativeName()));
        return queryResults.stream().map(IdArchive::new).collect(Collectors.toList());
    }


    @NotNull
    private Element createEagElement(@NotNull String shortName, @NotNull String archiveName,
                                     @NotNull String countrycode, @NotNull Desc desc) {

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
                                                    @Nullable Region region, @NotNull Address address,
                                                    @NotNull ContactInformation contactInformation,
                                                    @NotNull String logoUrl) {

        IdArchive id = new IdArchive(shortName);
        AtomAuthor atomAuthor = new AtomAuthor(authorEmail);
        String now = momcaConnection.queryDatabase(ExistQueryFactory.getCurrentDateTime()).get(0);

        String regionNativeName = region == null ? "" : region.getNativeName();
        Desc desc = new Desc(country.getNativeName(), regionNativeName, address, contactInformation, logoUrl);
        Element eag = createEagElement(shortName, name, country.getCountryCode().getCode(), desc);

        return new Entry(id, atomAuthor, now, eag);

    }


}
