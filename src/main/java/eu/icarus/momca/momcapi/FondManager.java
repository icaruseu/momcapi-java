package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.Archive;
import eu.icarus.momca.momcapi.resource.Fond;
import eu.icarus.momca.momcapi.resource.MomcaResource;
import eu.icarus.momca.momcapi.xml.atom.IdFond;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 09/08/2015.
 */
public class FondManager extends AbstractManager {

    public FondManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @NotNull
    public Optional<Fond> getFond(@NotNull IdFond idFond) {

        Optional<Fond> fond = Optional.empty();

        Optional<MomcaResource> fondResource = getMomcaResource(idFond);

        if (fondResource.isPresent()) {

            String prefsUrl = fondResource.get().getUri().replace("ead", "preferences");
            Optional<MomcaResource> fondPrefs = getMomcaResource(prefsUrl);

            fond = Optional.of(new Fond(fondResource.get(), fondPrefs));

        }

        return fond;

    }

    @NotNull
    public List<IdFond> listFondsForArchive(@NotNull Archive archive) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listFondsForArchive(archive.getId().getArchiveIdentifier()));
        return queryResults.stream().map(IdFond::new).collect(Collectors.toList());
    }

}
