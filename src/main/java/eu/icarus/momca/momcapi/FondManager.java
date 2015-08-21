package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.*;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 09/08/2015.
 */
public class FondManager extends AbstractManager {

    private static final String EAD_TEMPLATE = "<ead:ead xmlns:ead=\"urn:isbn:1-931666-22-9\"><ead:eadheader><ead:eadid /><ead:filedesc><ead:titlestmt><ead:titleproper /><ead:author /></ead:titlestmt></ead:filedesc></ead:eadheader><ead:archdesc level=\"otherlevel\"><ead:did><ead:abstract /></ead:did><ead:dsc><ead:c level=\"fonds\"><ead:did><ead:unitid identifier=\"%s\">%s</ead:unitid><ead:unittitle>%s</ead:unittitle></ead:did><ead:bioghist><ead:head /><ead:p /></ead:bioghist><ead:custodhist><ead:head /><ead:p /></ead:custodhist><ead:bibliography><ead:bibref /></ead:bibliography><ead:odd><ead:head /><ead:p /></ead:odd></ead:c></ead:dsc></ead:archdesc></ead:ead>";
    private static final String PREFERENCES_TEMPLATE = "<xrx:preferences xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:param name=\"image-access\">%s</xrx:param><xrx:param name=\"dummy-image-url\">%s</xrx:param><xrx:param name=\"image-server-base-url\" >%s</xrx:param></xrx:preferences>";

    public FondManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @NotNull
    public Fond addFond(@NotNull IdUser userId, @NotNull Archive archive, @NotNull String identifier,
                        @NotNull String name, @Nullable ImageAccess imageAccess, @Nullable URL imagesUrl,
                        @Nullable URL dummyImageUrl) {

        if (!momcaConnection.getUserManager().getUser(userId).isPresent()) {
            String message = String.format("The author '%s' is not existing in the database.", userId);
            throw new IllegalArgumentException(message);
        }

        if (!momcaConnection.getArchiveManager().getArchive(archive.getId()).isPresent()) {
            String message = String.format(
                    "The archive you're trying to add a fond into doesn't exist in the database: %s",
                    archive.getId().getIdentifier());
            throw new IllegalArgumentException(message);
        }

        IdFond id = new IdFond(archive.getIdentifier(), identifier);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The fond about to be added is not allowed to have an empty name!");
        }

        if (getFond(id).isPresent()) {
            String message = String.format("An fond for the id '%s' is already existing.", id.getIdentifier());
            throw new IllegalArgumentException(message);
        }

        String archiveCollectionUri = ResourceRoot.ARCHIVAL_FONDS.getUri() + "/" + archive.getIdentifier();
        String fondCollectionUri = archiveCollectionUri + "/" + identifier;

        String eadName = identifier + ".ead.xml";
        Element eadContent = createEadContent(userId, id.getContentXml(), identifier, name);
        ExistResource eadResource = new ExistResource(eadName, fondCollectionUri, eadContent.toXML());

        String preferencesName = identifier + ".preferences.xml";
        Optional<Element> preferencesContent = createPreferencesContent(imageAccess, imagesUrl, dummyImageUrl);
        Optional<ExistResource> preferencesResource = preferencesContent.map(element
                -> new ExistResource(preferencesName, fondCollectionUri, element.toXML()));


        momcaConnection.addCollection(archive.getIdentifier(), ResourceRoot.ARCHIVAL_FONDS.getUri());
        momcaConnection.addCollection(identifier, archiveCollectionUri);

        momcaConnection.storeExistResource(eadResource);
        preferencesResource.ifPresent(momcaConnection::storeExistResource);

        return getFond(id).orElseThrow(RuntimeException::new);

    }

    @NotNull
    private AtomEntry createEadContent(@NotNull IdUser author, @NotNull AtomId atomId, @NotNull String
            identifier, @NotNull String name) {

        String eadString = String.format(EAD_TEMPLATE, identifier, identifier, name);
        Element eadElement = Util.parseToElement(eadString);

        String now = momcaConnection.queryDatabase(ExistQueryFactory.getCurrentDateTime()).get(0);
        return new AtomEntry(atomId, author.getContentXml(), now, eadElement);

    }

    @NotNull
    private Optional<Element> createPreferencesContent(@Nullable ImageAccess imageAccess, @Nullable URL
            imagesUrl, @Nullable URL dummyImageUrl) {

        Optional<Element> preferencesXml = Optional.empty();

        if (imageAccess != null || imagesUrl != null || dummyImageUrl != null) {

            String imageAccessString = imageAccess == null ? ImageAccess.FREE.getText() : imageAccess.getText();
            String imagesUrlString = imagesUrl == null ? "" : imagesUrl.toExternalForm();
            String dummyImageUrlString = dummyImageUrl == null ? "" : dummyImageUrl.toExternalForm();

            String preferencesXmlString = String.format(PREFERENCES_TEMPLATE,
                    imageAccessString,
                    dummyImageUrlString,
                    imagesUrlString);

            preferencesXml = Optional.of(Util.parseToElement(preferencesXmlString));

        }

        return preferencesXml;

    }

    public void deleteFond(@NotNull IdFond idFond) {

        if (!momcaConnection.getCharterManager().listChartersPublic(idFond).isEmpty()
                || !momcaConnection.getCharterManager().listChartersImport(idFond).isEmpty()) {
            throw new IllegalArgumentException("There are still existing charters for fond '" + idFond.getIdentifier() + "'");
        }

        momcaConnection.deleteCollection(String.format("%s/%s/%s", ResourceRoot.PUBLIC_CHARTERS.getUri(), idFond.getIdArchive().getIdentifier(), idFond.getIdentifier()));
        momcaConnection.deleteCollection(String.format("%s/%s/%s", ResourceRoot.ARCHIVAL_FONDS.getUri(), idFond.getIdArchive().getIdentifier(), idFond.getIdentifier()));

    }

    @NotNull
    public Optional<Fond> getFond(@NotNull IdFond idFond) {

        Optional<Fond> fond = Optional.empty();

        Optional<ExistResource> fondResource = getMomcaResource(idFond.getContentXml());

        if (fondResource.isPresent()) {

            String prefsUrl = fondResource.get().getUri().replace("ead", "preferences");
            Optional<ExistResource> fondPrefs = getMomcaResource(prefsUrl);

            fond = Optional.of(new Fond(fondResource.get(), fondPrefs));

        }

        return fond;

    }

    @NotNull
    public List<IdFond> listFonds(@NotNull IdArchive idArchive) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listFonds(idArchive));
        return queryResults.stream().map(AtomId::new).map(IdFond::new).collect(Collectors.toList());
    }

}
