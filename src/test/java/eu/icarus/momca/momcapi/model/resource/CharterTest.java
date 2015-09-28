package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.TestUtils;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.Date;
import eu.icarus.momca.momcapi.model.id.IdCharter;
import eu.icarus.momca.momcapi.model.xml.cei.DateExact;
import eu.icarus.momca.momcapi.model.xml.cei.Figure;
import eu.icarus.momca.momcapi.model.xml.cei.Idno;
import eu.icarus.momca.momcapi.model.xml.cei.Witness;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.*;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by daniel on 27.06.2015.
 */
public class CharterTest {

    private Charter charter;

    @NotNull
    private Charter createCharter(String fileName) throws ParsingException, IOException {
        ExistResource resource = getExistResource(fileName);
        return new Charter(resource);
    }

    @NotNull
    private static ExistResource getExistResource(String fileName) throws ParsingException, IOException {
        Element element = (Element) TestUtils.getXmlFromResource(fileName).getRootElement().copy();
        String uri = "/db/mom-data/metadata.charter.public/collection";
        return new ExistResource(fileName, uri, element.toXML());
    }

    @BeforeMethod
    public void setUp() throws Exception {

        IdCharter id = new IdCharter("collection", "charter1");
        Date date = new Date(new DateExact("14180201", "February 1st, 1418"));
        User user = new User("user", "moderator");

        charter = new Charter(id, CharterStatus.PUBLIC, user, date);

    }

    @Test
    public void testConstructor1() throws Exception {

        IdCharter id = new IdCharter("collection", "charter1");
        Date date = new Date(new DateExact("14180201", "February 1st, 1418"));
        User user = new User("user", "moderator");

        charter = new Charter(id, CharterStatus.PUBLIC, user, date);

        assertEquals(charter.getCharterStatus(), CharterStatus.PUBLIC);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.public/collection");
        assertEquals(charter.getResourceName(), "charter1.cei.xml");

        assertEquals(charter.getId(), id);
        assertTrue(charter.getCreator().isPresent());
        assertEquals(charter.getCreator().get(), user.getId());
        assertEquals(charter.getIdno().getId(), "charter1");
        assertEquals(charter.getIdno().getText(), "charter1");
        assertEquals(charter.getDate(), date);

        assertEquals(charter.getBackDivNotes().size(), 0);
        assertEquals(charter.getBackPersNames().size(), 0);
        assertEquals(charter.getBackGeogNames().size(), 0);
        assertEquals(charter.getBackIndexes().size(), 0);

        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

    }

    @Test
    public void testConstructor2WithEmptyCharter() throws Exception {

        Date date = new Date();
        User user = new User("guest", "moderator");

        charter = createCharter("empty_charter.xml");

        assertEquals(charter.getCharterStatus(), CharterStatus.PUBLIC);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.public/collection");
        assertEquals(charter.getResourceName(), "empty_charter.xml");

        assertEquals(charter.getId(), new IdCharter("collection", "empty_charter"));
        assertTrue(charter.getCreator().isPresent());
        assertEquals(charter.getCreator().get(), user.getId());
        assertEquals(charter.getIdno().getId(), "empty_charter");
        assertEquals(charter.getIdno().getText(), "New Charter");
        assertEquals(charter.getDate(), date);

        assertFalse(charter.getTenor().isPresent());
        assertFalse(charter.getAbstract().isPresent());

        charter.setAbstract(new Abstract("New Abstract"));

        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"empty_charter\">New Charter</cei:idno><cei:chDesc><cei:abstract>New Abstract</cei:abstract><cei:issued><cei:date value=\"99999999\" /></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

    }

    @Test
    public void testConstructor2WithTestCharter1() throws Exception {

        Date date = new Date(LocalDate.of(947, 10, 27), "0947-10-27");

        charter = createCharter("testcharter1.xml");

        assertEquals(charter.getCharterStatus(), CharterStatus.PUBLIC);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.public/collection");
        assertEquals(charter.getResourceName(), "testcharter1.xml");

        assertEquals(charter.getId(), new IdCharter("collection", "KAE_Urkunde_Nr_1"));
        assertFalse(charter.getCreator().isPresent());
        assertEquals(charter.getIdno().getId(), "KAE_Urkunde_Nr_1");
        assertEquals(charter.getIdno().getText(), "KAE, Urkunde Nr. 1");
        assertEquals(charter.getIdno().getFacs().get(), "facs");
        assertEquals(charter.getIdno().getN().get(), "n");
        assertTrue(charter.getIdno().getOld().isPresent());
        assertEquals(charter.getIdno().getOld().get(), "1");
        assertEquals(charter.getDate(), date);
        assertEquals(charter.getDate().getLiteralDate(), "0947-10-27");
        assertEquals(charter.getDate().getCertainty().get(), "certainty");
        assertEquals(charter.getDate().getLang().get(), "lang");
        assertEquals(charter.getDate().getFacs().get(), "facs");
        assertEquals(charter.getDate().getId().get(), "id");
        assertEquals(charter.getDate().getN().get(), "n");

        assertTrue(charter.getSourceDescAbstractBibliography().isPresent());
        assertEquals(charter.getSourceDescAbstractBibliography().get().getEntries().size(), 1);
        assertEquals(charter.getSourceDescAbstractBibliography().get().getEntries().get(0).getContent(), "QW I/1, Nr. 28");
        assertEquals(charter.getSourceDescAbstractBibliography().get().getEntries().get(0).getFacs().get(), "facs");
        assertEquals(charter.getSourceDescAbstractBibliography().get().getEntries().get(0).getLang().get(), "lang");
        assertEquals(charter.getSourceDescAbstractBibliography().get().getEntries().get(0).getId().get(), "id");
        assertEquals(charter.getSourceDescAbstractBibliography().get().getEntries().get(0).getN().get(), "n");
        assertEquals(charter.getSourceDescAbstractBibliography().get().getEntries().get(0).getKey().get(), "key");
        assertTrue(charter.getSourceDescTenorBibliography().isPresent());
        assertEquals(charter.getSourceDescTenorBibliography().get().getEntries().size(), 1);
        assertEquals(charter.getSourceDescTenorBibliography().get().getEntries().get(0).getContent(), "Volltext");
        assertFalse(charter.getSourceDescTenorBibliography().get().getEntries().get(0).getFacs().isPresent());

        assertTrue(charter.getTenor().isPresent());
        assertEquals(charter.getTenor().get().getContent(), "This is the <cei:hi>Winter</cei:hi> of our discontempt.");
        assertEquals(charter.getTenor().get().getFacs().get(), "facs");
        assertEquals(charter.getTenor().get().getId().get(), "id");
        assertEquals(charter.getTenor().get().getN().get(), "n");

        assertTrue(charter.getAbstract().isPresent());
        assertEquals(charter.getAbstract().get().getContent(), "König Otto I. verleiht auf Bitte Herzog Hermanns dem Kloster Meinradszell (Einsiedeln), das samt einer Kirche vom jetzigen <cei:persName>Abt Eberhard</cei:persName> auf Boden, der dem Herzog von einigen Getreuen zu eigen gegeben worden war, mit dessen Unterstützung errichtet worden ist, das Recht freier Wahl des Abtes nach dem Tode Eberhards und Immunität.");
        assertEquals(charter.getAbstract().get().getFacs().get(), "facs");
        assertEquals(charter.getAbstract().get().getId().get(), "id");
        assertEquals(charter.getAbstract().get().getLang().get(), "lang");
        assertEquals(charter.getAbstract().get().getN().get(), "n");

        assertTrue(charter.getLangMom().isPresent());
        assertEquals(charter.getLangMom().get(), "Latein");

        assertTrue(charter.getCharterClass().isPresent());
        assertEquals(charter.getCharterClass().get(), "Urkunde");

        assertTrue(charter.getIssuedPlace().isPresent());
        assertEquals(charter.getIssuedPlace().get().getContent(),
                new PlaceName("Frankfurt <cei:hi>am Main</cei:hi>", "", "", "City", "", "").getContent());

        assertEquals(charter.getBackPlaceNames().size(), 2);
        assertEquals(charter.getBackPlaceNames().get(0).getContent(), "Frankfurt");
        assertEquals(charter.getBackPlaceNames().get(0).getCertainty().get(), "certainty");
        assertEquals(charter.getBackPlaceNames().get(0).getExistent().get(), "existent");
        assertEquals(charter.getBackPlaceNames().get(0).getFacs().get(), "facs");
        assertEquals(charter.getBackPlaceNames().get(0).getId().get(), "id");
        assertEquals(charter.getBackPlaceNames().get(0).getKey().get(), "key");
        assertEquals(charter.getBackPlaceNames().get(0).getLang().get(), "lang");
        assertEquals(charter.getBackPlaceNames().get(0).getN().get(), "n");
        assertEquals(charter.getBackPlaceNames().get(0).getReg().get(), "reg");
        assertEquals(charter.getBackPlaceNames().get(0).getType().get(), "type");

        assertEquals(charter.getBackGeogNames().size(), 2);
        assertEquals(charter.getBackGeogNames().get(0).getContent(), "Ein Berg");
        assertEquals(charter.getBackGeogNames().get(0).getCertainty().get(), "certainty");
        assertEquals(charter.getBackGeogNames().get(0).getExistent().get(), "existent");
        assertEquals(charter.getBackGeogNames().get(0).getFacs().get(), "facs");
        assertEquals(charter.getBackGeogNames().get(0).getId().get(), "id");
        assertEquals(charter.getBackGeogNames().get(0).getKey().get(), "key");
        assertEquals(charter.getBackGeogNames().get(0).getLang().get(), "lang");
        assertEquals(charter.getBackGeogNames().get(0).getN().get(), "n");
        assertEquals(charter.getBackGeogNames().get(0).getReg().get(), "reg");
        assertEquals(charter.getBackGeogNames().get(0).getType().get(), "type");

        assertEquals(charter.getBackPersNames().size(), 2);
        assertEquals(charter.getBackPersNames().get(1).getContent(), "Eginhardus");
        assertEquals(charter.getBackPersNames().get(1).getCertainty().get(), "certainty");
        assertEquals(charter.getBackPersNames().get(1).getFacs().get(), "facs");
        assertEquals(charter.getBackPersNames().get(1).getId().get(), "id");
        assertEquals(charter.getBackPersNames().get(1).getKey().get(), "key");
        assertEquals(charter.getBackPersNames().get(1).getLang().get(), "lang");
        assertEquals(charter.getBackPersNames().get(1).getN().get(), "n");
        assertEquals(charter.getBackPersNames().get(1).getReg().get(), "reg");
        assertEquals(charter.getBackPersNames().get(1).getType().get(), "type");

        assertEquals(charter.getBackIndexes().size(), 1);
        assertEquals(charter.getBackIndexes().get(0).getContent(), "Schwert");
        assertEquals(charter.getBackIndexes().get(0).getType().get(), "type");
        assertEquals(charter.getBackIndexes().get(0).getId().get(), "id");
        assertEquals(charter.getBackIndexes().get(0).getN().get(), "n");
        assertEquals(charter.getBackIndexes().get(0).getLemma().get(), "lemma");
        assertEquals(charter.getBackIndexes().get(0).getSublemma().get(), "sublemma");
        assertEquals(charter.getBackIndexes().get(0).getLang().get(), "lang");
        assertEquals(charter.getBackIndexes().get(0).getIndexName().get(), "indexName");
        assertEquals(charter.getBackIndexes().get(0).getFacs().get(), "facs");

        assertEquals(charter.getBackDivNotes().size(), 1);
        assertEquals(charter.getBackDivNotes().get(0).getPlace().get(), "§1");
        assertEquals(charter.getBackDivNotes().get(0).getId().get(), "id");
        assertEquals(charter.getBackDivNotes().get(0).getN().get(), "n");
        assertEquals(charter.getBackDivNotes().get(0).getContent(), "Unbekannt");

        assertEquals(charter.getWitListPar().size(), 1);
        assertEquals(charter.getWitListPar().get(0).getTraditioForm().get().getContent(), "Kopie");
        assertEquals(charter.getWitListPar().get(0).getFigures().size(), 1);

        assertTrue(charter.getWitnessOrig().isPresent());
        assertEquals(charter.getWitnessOrig().get().getFigures().size(), 2);
        assertEquals(charter.getWitnessOrig().get().getTraditioForm().get().getContent(), "Original");

        charter.regenerateXmlContent();

        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front><cei:sourceDesc><cei:sourceDescRegest><cei:bibl facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\">QW I/1, Nr. 28</cei:bibl></cei:sourceDescRegest><cei:sourceDescVolltext id=\"id\"><cei:bibl>Volltext</cei:bibl></cei:sourceDescVolltext></cei:sourceDesc><cei:publicationStmt>\n" +
                "                    <cei:p>Published 2013</cei:p>\n" +
                "                </cei:publicationStmt></cei:front><cei:body><cei:idno facs=\"facs\" id=\"KAE_Urkunde_Nr_1\" n=\"n\" old=\"1\">KAE, Urkunde Nr. 1</cei:idno><cei:chDesc><cei:class>Urkunde</cei:class><cei:abstract facs=\"facs\" id=\"id\" lang=\"lang\" n=\"n\">König Otto I. verleiht auf Bitte Herzog Hermanns dem Kloster Meinradszell (Einsiedeln), das samt einer Kirche vom jetzigen <cei:persName>Abt Eberhard</cei:persName> auf Boden, der dem Herzog von einigen Getreuen zu eigen gegeben worden war, mit dessen Unterstützung errichtet worden ist, das Recht freier Wahl des Abtes nach dem Tode Eberhards und Immunität.</cei:abstract><cei:issued><cei:placeName lang=\"lang\" type=\"City\">Frankfurt <cei:hi>am Main</cei:hi></cei:placeName><cei:date certainty=\"certainty\" facs=\"facs\" id=\"id\" lang=\"lang\" n=\"n\" value=\"9471027\">0947-10-27</cei:date></cei:issued><cei:witnessOrig n=\"A\"><cei:traditioForm>Original</cei:traditioForm><cei:archIdentifier>\n" +
                "                            <cei:arch>Klosterarchiv Einsiedeln</cei:arch>\n" +
                "                            <cei:idno>KAE, A.BI.1</cei:idno>\n" +
                "                        </cei:archIdentifier><cei:auth>\n" +
                "                            <cei:sealDesc>1 Siegel</cei:sealDesc>\n" +
                "                        </cei:auth><cei:figure><cei:graphic url=\"KAE_A_BI_1-v.jpg\" /></cei:figure><cei:figure><cei:graphic url=\"KAE_A_BI_1-r.jpg\" /></cei:figure></cei:witnessOrig><cei:witListPar><cei:witness n=\"B\"><cei:traditioForm>Kopie</cei:traditioForm><cei:archIdentifier>\n" +
                "                                <cei:arch>Klosterarchiv Einsiedeln</cei:arch>\n" +
                "                                <cei:idno>KAE, A.II.1</cei:idno>\n" +
                "                            </cei:archIdentifier><cei:physicalDesc /><cei:figure><cei:graphic url=\"http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0067.jpg\" /></cei:figure></cei:witness></cei:witListPar><cei:diplomaticAnalysis>\n" +
                "                        <cei:listBiblRegest>\n" +
                "                            <cei:bibl facs=\"1\">Morel, Nr. 1.</cei:bibl>\n" +
                "                            <cei:bibl>Regesta imperii II/1, 1, Nr. 157.</cei:bibl>\n" +
                "                            <cei:bibl>Helbok, Regesten Vorarlberg, Nr. 132.</cei:bibl>\n" +
                "                            <cei:bibl>UB Südl. St. Gallen, Band I, Nr. 67.</cei:bibl>\n" +
                "                            <cei:bibl>Hidber, Urkundenregister, Band I, Nr. 1025.</cei:bibl>\n" +
                "                        </cei:listBiblRegest>\n" +
                "                        <cei:listBiblEdition>\n" +
                "                            <cei:bibl>MGH DO I, Nr. 94.</cei:bibl>\n" +
                "                            <cei:bibl>DAE, Band G, Nr. 25, S. 25.</cei:bibl>\n" +
                "                            <cei:bibl>QW I/1, Nr. 28.</cei:bibl>\n" +
                "                            <cei:bibl>Gfr, Band 43, 1888, S. 322f..</cei:bibl>\n" +
                "                        </cei:listBiblEdition>\n" +
                "                        <cei:listBiblErw>\n" +
                "                            <cei:bibl>Sickel, Kaiserurkunden, S. 70, 72-77.</cei:bibl>\n" +
                "                            <cei:bibl>MGH Ergänzungen, Nr. O.I.094.</cei:bibl>\n" +
                "                        </cei:listBiblErw>\n" +
                "                    </cei:diplomaticAnalysis><cei:lang_MOM>Latein</cei:lang_MOM></cei:chDesc><cei:tenor facs=\"facs\" id=\"id\" n=\"n\">This is the <cei:hi>Winter</cei:hi> of our discontempt.</cei:tenor></cei:body><cei:back><cei:persName reg=\"Karl der Große\" type=\"Kaiser\">Carolus <cei:hi>Magnus</cei:hi></cei:persName><cei:persName certainty=\"certainty\" facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\" reg=\"reg\" type=\"type\">Eginhardus</cei:persName><cei:placeName certainty=\"certainty\" existent=\"existent\" facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\" reg=\"reg\" type=\"type\">Frankfurt</cei:placeName><cei:placeName reg=\"Einsiedeln\">Kloster <cei:hi>Einsiedeln</cei:hi> in der Schweiz</cei:placeName><cei:geogName certainty=\"certainty\" existent=\"existent\" facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\" reg=\"reg\" type=\"type\">Ein Berg</cei:geogName><cei:geogName>Der <cei:hi>Grabeshügel</cei:hi></cei:geogName><cei:index facs=\"facs\" id=\"id\" indexName=\"indexName\" lang=\"lang\" lemma=\"lemma\" n=\"n\" sublemma=\"sublemma\" type=\"type\">Schwert</cei:index><cei:divNotes><cei:note place=\"§1\" id=\"id\" n=\"n\">Unbekannt</cei:note></cei:divNotes></cei:back></cei:text>");

    }

    @Test
    public void testGetId() throws Exception {
        charter = createCharter("empty_charter.xml");
        assertEquals(charter.getId(), new IdCharter("collection", "empty_charter"));
    }

    @Test
    public void testGetIdentifier() throws Exception {
        charter = createCharter("empty_charter.xml");
        assertEquals(charter.getIdentifier(), "empty_charter");
    }

    @Test
    public void testGetValidationProblems() throws Exception {

        charter = createCharter("invalid_charter.xml");

        assertEquals(charter.getValidationProblems().size(), 1);
        assertEquals(charter.getValidationProblems().get(0).getMessage(), "cvc-complex-type.2.4.a: Invalid content was found starting with element 'cei:idno'. One of '{\"http://www.monasterium.net/NS/cei\":persName, \"http://www.monasterium.net/NS/cei\":placeName, \"http://www.monasterium.net/NS/cei\":geogName, \"http://www.monasterium.net/NS/cei\":index, \"http://www.monasterium.net/NS/cei\":testis, \"http://www.monasterium.net/NS/cei\":date, \"http://www.monasterium.net/NS/cei\":dateRange, \"http://www.monasterium.net/NS/cei\":num, \"http://www.monasterium.net/NS/cei\":measure, \"http://www.monasterium.net/NS/cei\":quote, \"http://www.monasterium.net/NS/cei\":cit, \"http://www.monasterium.net/NS/cei\":foreign, \"http://www.monasterium.net/NS/cei\":anchor, \"http://www.monasterium.net/NS/cei\":ref, \"http://www.monasterium.net/NS/cei\":hi, \"http://www.monasterium.net/NS/cei\":lb, \"http://www.monasterium.net/NS/cei\":pb, \"http://www.monasterium.net/NS/cei\":sup, \"http://www.monasterium.net/NS/cei\":c, \"http://www.monasterium.net/NS/cei\":recipient, \"http://www.monasterium.net/NS/cei\":issuer}' is expected.");

    }

    @Test
    public void testIsValidCei() throws Exception {
        Charter charter = createCharter("invalid_charter.xml");
        assertFalse(charter.isValidCei());
    }

    @Test
    public void testSetAbstract() throws Exception {

        assertFalse(charter.getAbstract().isPresent());

        Abstract charterAbstract = new Abstract("An abstract with an <cei:issuer>issuer</cei:issuer>", "facs", "id", "lang", "n");
        charter.setAbstract(charterAbstract);

        assertTrue(charter.getAbstract().isPresent());
        assertEquals(charter.getAbstract().get().getContent(), "An abstract with an <cei:issuer>issuer</cei:issuer>");
        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:abstract facs=\"facs\" id=\"id\" lang=\"lang\" n=\"n\">An abstract with an <cei:issuer>issuer</cei:issuer></cei:abstract><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

        charter.setAbstract(new Abstract(""));

        assertTrue(charter.isValidCei());
        assertFalse(charter.getAbstract().isPresent());

    }

    @Test
    public void testSetBackDivNotes() throws Exception {

        assertEquals(charter.getBackDivNotes().size(), 0);

        Note note1 = new Note("Note1", "Somewhere", "id", "n");
        Note note2 = new Note("Note2", "Over");
        List<Note> notes = new ArrayList<>(2);
        notes.add(note1);
        notes.add(note2);

        charter.setBackDivNotes(notes);

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackDivNotes().size(), 2);
        assertEquals(charter.getBackDivNotes().get(1).toXML(), note2.toXML());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back><cei:divNotes><cei:note place=\"Somewhere\" id=\"id\" n=\"n\">Note1</cei:note><cei:note place=\"Over\">Note2</cei:note></cei:divNotes></cei:back></cei:text>");

        charter.setBackDivNotes(new ArrayList<>(0));

        assertTrue(charter.isValidCei());
        assertTrue(charter.getBackDivNotes().isEmpty());

    }

    @Test
    public void testSetBackGeogNames() throws Exception {

        GeogName place1 = new GeogName("Ein Berg", "certainty", "reg", "type", "existent", "key", "facs", "id", "lang", "n");
        GeogName place2 = new GeogName("Ein Hügel", "", "Hügel", "Ding", "", "");

        List<GeogName> geogNames = new ArrayList<>(0);
        geogNames.add(place1);
        geogNames.add(place2);

        charter.setBackGeogNames(geogNames);

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackGeogNames(), geogNames);
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back><cei:geogName certainty=\"certainty\" existent=\"existent\" facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\" reg=\"reg\" type=\"type\">Ein Berg</cei:geogName><cei:geogName reg=\"Hügel\" type=\"Ding\">Ein Hügel</cei:geogName></cei:back></cei:text>");

        charter.setBackGeogNames(new ArrayList<>(0));

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackGeogNames().size(), 0);

    }

    @Test
    public void testSetBackIndexes() throws Exception {

        assertEquals(charter.getBackIndexes().size(), 0);

        Index index1 = new Index("Schwert", "Waffen", "", "", "");
        Index index2 = new Index("Axt", "Waffen", "a", "b", "c", "d", "e", "f", "g");
        List<Index> indexes = new ArrayList<>(2);
        indexes.add(index1);
        indexes.add(index2);

        charter.setBackIndexes(indexes);

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackIndexes().size(), 2);
        assertEquals(charter.getBackIndexes().get(1).getContent(), index2.getContent());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back><cei:index indexName=\"Waffen\">Schwert</cei:index><cei:index facs=\"e\" id=\"d\" indexName=\"Waffen\" lang=\"f\" lemma=\"a\" n=\"g\" sublemma=\"b\" type=\"c\">Axt</cei:index></cei:back></cei:text>");

        charter.setBackIndexes(new ArrayList<>(0));

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackIndexes().size(), 0);

    }

    @Test
    public void testSetBackPersNames() throws Exception {

        PersName name1 = new PersName("Carolus <cei:hi>Magnus</cei:hi>", "", "", "Karl der Große", "Kaiser");
        PersName name2 = new PersName("Einhard", "certainty", "facs", "id", "key", "lang", "n", "reg", "type");
        List<PersName> names = new ArrayList<>(0);
        names.add(name1);
        names.add(name2);

        assertEquals(charter.getBackPersNames().size(), 0);

        charter.setBackPersNames(names);

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackPersNames().size(), 2);
        assertEquals(charter.getBackPersNames(), names);
        assertEquals(charter.getBackPersNames().get(1).getContent(), "Einhard");

        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back><cei:persName reg=\"Karl der Große\" type=\"Kaiser\">Carolus <cei:hi>Magnus</cei:hi></cei:persName><cei:persName certainty=\"certainty\" facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\" reg=\"reg\" type=\"type\">Einhard</cei:persName></cei:back></cei:text>");

        charter.setBackPersNames(new ArrayList<>(0));

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackPersNames().size(), 0);

    }

    @Test
    public void testSetBackPlaceNames() throws Exception {

        PlaceName place1 = new PlaceName("Frankfurt", "certainty", "reg", "type", "existent", "key", "facs", "id", "lang", "n");
        PlaceName place2 = new PlaceName("Iuvavum", "", "Salzburg", "City", "", "");

        List<PlaceName> placeNames = new ArrayList<>(0);
        placeNames.add(place1);
        placeNames.add(place2);

        charter.setBackPlaceNames(placeNames);

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackPlaceNames(), placeNames);
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back><cei:placeName certainty=\"certainty\" existent=\"existent\" facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\" reg=\"reg\" type=\"type\">Frankfurt</cei:placeName><cei:placeName reg=\"Salzburg\" type=\"City\">Iuvavum</cei:placeName></cei:back></cei:text>");

        charter.setBackPlaceNames(new ArrayList<>(0));

        assertTrue(charter.isValidCei());
        assertEquals(charter.getBackPlaceNames().size(), 0);

    }

    @Test
    public void testSetCharterClass() throws Exception {


    }

    @Test
    public void testSetCharterStatus() throws Exception {

        charter.setCharterStatus(CharterStatus.IMPORTED);
        assertEquals(charter.getCharterStatus(), CharterStatus.IMPORTED);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.import/collection");
        assertEquals(charter.getResourceName(), "charter1.cei.xml");
        assertTrue(charter.isValidCei());

        charter.setCharterStatus(CharterStatus.PRIVATE);
        assertEquals(charter.getCharterStatus(), CharterStatus.PRIVATE);
        assertEquals(charter.getParentUri(), "/db/mom-data/xrx.user/user/metadata.charter/collection");
        assertEquals(charter.getResourceName(), "charter1.charter.xml");
        assertTrue(charter.isValidCei());

        charter.setCharterStatus(CharterStatus.SAVED);
        assertEquals(charter.getCharterStatus(), CharterStatus.SAVED);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.saved");
        assertEquals(charter.getResourceName(), "tag%3Awww.monasterium.net%2C2011%3A%23charter%23collection%23charter1.xml");
        assertTrue(charter.isValidCei());

    }

    @Test
    public void testSetDate() throws Exception {

        Date newDate = new Date(LocalDate.of(1218, 6, 19), 0, "19th June, 1218", "certainty", "lang", "facs", "id", "n");

        charter.setDate(newDate);

        assertEquals(charter.getDate(), newDate);
        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date certainty=\"certainty\" facs=\"facs\" id=\"id\" lang=\"lang\" n=\"n\" value=\"12180619\">19th June, 1218</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

    }

    @Test
    public void testSetIdentifier() throws Exception {

        charter = createCharter("empty_charter.xml");
        String new_identifier = "new_identifier";
        charter.setIdentifier(new_identifier);

        assertEquals(charter.getIdentifier(), new_identifier);
        assertEquals(charter.getId(), new IdCharter("collection", new_identifier));
        assertTrue(charter.isValidCei());

    }

    @Test
    public void testSetIdno() throws Exception {

        assertEquals(charter.getIdno().getId(), "charter1");

        Idno newId = new Idno("New Idno Text", "facs", "newId", "n", "old");
        charter.setIdno(newId);

        assertEquals(charter.getIdno(), newId);
        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno facs=\"facs\" id=\"newId\" n=\"n\" old=\"old\">New Idno Text</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

    }

    @Test
    public void testSetIssuedPlace() throws Exception {

        assertFalse(charter.getIssuedPlace().isPresent());

        PlaceName placeName = new PlaceName("Iuvavum", "", "Salzburg", "City", "", "");
        charter.setIssuedPlace(placeName);

        assertTrue(charter.getIssuedPlace().isPresent());
        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:placeName reg=\"Salzburg\" type=\"City\">Iuvavum</cei:placeName><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

        charter.setIssuedPlace(new PlaceName(""));

        assertFalse(charter.getIssuedPlace().isPresent());
        assertTrue(charter.isValidCei());

    }

    @Test
    public void testSetLangMom() throws Exception {

        assertFalse(charter.getLangMom().isPresent());

        charter.setLangMom("Deutsch");

        assertTrue(charter.getLangMom().isPresent());
        assertEquals(charter.getLangMom().get(), "Deutsch");
        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /><cei:lang_MOM>Deutsch</cei:lang_MOM></cei:chDesc></cei:body><cei:back /></cei:text>");

        charter.setLangMom("");
        assertFalse(charter.getLangMom().isPresent());
        assertTrue(charter.isValidCei());

    }

    @Test
    public void testSetSourceDescAbstractBibliography() throws Exception {

        assertFalse(charter.getSourceDescAbstractBibliography().isPresent());

        Bibl bibl1 = new Bibl("bibl1", "facs", "id", "key", "lang", "n");
        Bibl bibl2 = new Bibl("bibl2");
        List<Bibl> bibl = new ArrayList<>(2);
        bibl.add(bibl1);
        bibl.add(bibl2);

        charter.setSourceDescAbstractBibliography(bibl);

        assertTrue(charter.isValidCei());
        assertTrue(charter.getSourceDescAbstractBibliography().isPresent());
        assertEquals(charter.getSourceDescAbstractBibliography().get().getEntries().size(), 2);
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front><cei:sourceDesc><cei:sourceDescRegest><cei:bibl facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\">bibl1</cei:bibl><cei:bibl>bibl2</cei:bibl></cei:sourceDescRegest></cei:sourceDesc></cei:front><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

        charter.setSourceDescAbstractBibliography(new ArrayList<>(0));

        assertTrue(charter.isValidCei());
        assertFalse(charter.getSourceDescAbstractBibliography().isPresent());

    }

    @Test
    public void testSetSourceDescTenorBibliography() throws Exception {

        assertFalse(charter.getSourceDescTenorBibliography().isPresent());

        Bibl bibl1 = new Bibl("bibl1", "facs", "id", "key", "lang", "n");
        Bibl bibl2 = new Bibl("bibl2");
        List<Bibl> bibl = new ArrayList<>(2);
        bibl.add(bibl1);
        bibl.add(bibl2);

        charter.setSourceDescTenorBibliography(bibl);

        assertTrue(charter.isValidCei());
        assertTrue(charter.getSourceDescTenorBibliography().isPresent());
        assertEquals(charter.getSourceDescTenorBibliography().get().getEntries().size(), 2);
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front><cei:sourceDesc><cei:sourceDescVolltext><cei:bibl facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\">bibl1</cei:bibl><cei:bibl>bibl2</cei:bibl></cei:sourceDescVolltext></cei:sourceDesc></cei:front><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

        charter.setSourceDescTenorBibliography(new ArrayList<>(0));

        assertTrue(charter.isValidCei());
        assertFalse(charter.getSourceDescTenorBibliography().isPresent());

    }

    @Test
    public void testSetTenor() throws Exception {

        assertFalse(charter.getTenor().isPresent());

        Tenor tenor = new Tenor("This is the winter of <cei:lb/> our <cei:hi>discontempt</cei:hi>!", "facs", "id", "n");

        charter.setTenor(tenor);

        assertEquals(charter.getTenor().get().getContent(), "This is the winter of <cei:lb /> our <cei:hi>discontempt</cei:hi>!");
        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc><cei:tenor facs=\"facs\" id=\"id\" n=\"n\">This is the winter of <cei:lb /> our <cei:hi>discontempt</cei:hi>!</cei:tenor></cei:body><cei:back /></cei:text>");

        charter.setTenor(new Tenor(""));
        assertFalse(charter.getTenor().isPresent());

    }

    @Test
    public void testSetWitListPar() throws Exception {

        Figure figure = new Figure("image1.jpg");
        List<Figure> figures = new ArrayList<>(1);
        figures.add(figure);
        Witness witness = new Witness(null, null, figures, "", "", "", null, null, null);

        List<Witness> witListPar = new ArrayList<>(1);
        witListPar.add(witness);

        assertTrue(charter.getWitListPar().isEmpty());

        charter.setWitListPar(witListPar);

        assertEquals(charter.getWitListPar().size(), 1);
        assertEquals(charter.getWitListPar().get(0).getFigures().get(0).getUrl(), "image1.jpg");
        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig /><cei:witListPar><cei:witness><cei:figure><cei:graphic url=\"image1.jpg\" /></cei:figure></cei:witness></cei:witListPar><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

    }

    @Test
    public void testSetWitnessOrig() throws Exception {


        Figure figure = new Figure("image1.jpg");
        List<Figure> figures = new ArrayList<>(1);
        figures.add(figure);
        Witness witness = new Witness(null, null, figures, "", "", "", null, null, null);

        assertFalse(charter.getWitnessOrig().isPresent());

        charter.setWitnessOrig(witness);

        assertTrue(charter.getWitnessOrig().isPresent());
        assertEquals(charter.getWitnessOrig().get().getFigures().get(0).getUrl(), "image1.jpg");
        assertTrue(charter.isValidCei());
        assertEquals(charter.toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"charter1\">charter1</cei:idno><cei:chDesc><cei:issued><cei:date value=\"14180201\">February 1st, 1418</cei:date></cei:issued><cei:witnessOrig><cei:figure><cei:graphic url=\"image1.jpg\" /></cei:figure></cei:witnessOrig><cei:witListPar /><cei:diplomaticAnalysis /></cei:chDesc></cei:body><cei:back /></cei:text>");

        charter.setWitnessOrig(null);
        assertFalse(charter.getWitnessOrig().isPresent());

    }

}