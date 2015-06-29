package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 27.06.2015.
 */
public class CharterTest {

    private static final CharterAtomId CHARTER_ATOM_ID = new CharterAtomId("tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1");
    private static final String NAME = "KAE_Urkunde_Nr_1.cei.xml";
    private static final String PARENT_URI = "/db/mom-data/metadata.charter.public/CH-KAE/Urkunden";
    private static final CharterStatus STATUS = CharterStatus.PUBLIC;
    private static final String WRONG_XML_CONTENT = "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\" />";
    private static final String XML_CONTENT = "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"> <atom:id>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1</atom:id> <atom:title /> <atom:published>2011-11-28T09:57:23.704+01:00</atom:published> <atom:updated>2011-11-28T09:57:23.704+01:00</atom:updated> <atom:author> <atom:email /> </atom:author> <app:control xmlns:app=\"http://www.w3.org/2007/app\"> <app:draft>no</app:draft> </app:control> <atom:content type=\"application/xml\"> <cei:text id=\"1\" n=\"1\" type=\"charter\" xmlns=\"http://www.monasterium.net/NS/cei\" xmlns:cei=\"http://www.monasterium.net/NS/cei\"> <cei:front> <cei:sourceDesc> <cei:sourceDescRegest> <cei:bibl>QW I/1, Nr. 28</cei:bibl> </cei:sourceDescRegest> </cei:sourceDesc> </cei:front> <cei:body> <cei:idno id=\"KAE_Urkunde_Nr_1\" old=\"1\">KAE, Urkunde Nr. 1</cei:idno> <cei:chDesc> <cei:abstract>König Otto I. verleiht auf Bitte Herzog Hermanns dem Kloster Meinradszell (Einsiedeln), das samt einer Kirche vom jetzigen <cei:persName>Abt Eberhard</cei:persName>auf Boden, der dem Herzog von einigen Getreuen zu eigen gegeben worden war, mit dessen Unterstützung errichtet worden ist, das Recht freier Wahl des Abtes nach dem Tode Eberhards und Immunität.</cei:abstract> <cei:issued> <cei:placeName>Frankfurt</cei:placeName> <cei:date value=\"09471027\">0947-10-27</cei:date> </cei:issued> <cei:witnessOrig n=\"A\"> <cei:archIdentifier> <cei:arch>Klosterarchiv Einsiedeln</cei:arch> <cei:idno>KAE, A.BI.1</cei:idno> </cei:archIdentifier> <cei:traditioForm>Original</cei:traditioForm> <cei:figure> <cei:graphic url=\"KAE_A_BI_1-v.jpg\" /> </cei:figure> <cei:figure> <cei:graphic url=\"KAE_A_BI_1-r.jpg\" /> </cei:figure> <cei:auth> <cei:sealDesc>1 Siegel</cei:sealDesc> </cei:auth> </cei:witnessOrig> <cei:witListPar> <cei:witness n=\"B\"> <cei:archIdentifier> <cei:arch>Klosterarchiv Einsiedeln</cei:arch> <cei:idno>KAE, A.II.1</cei:idno> </cei:archIdentifier> <cei:traditioForm>Kopie</cei:traditioForm> <cei:figure> <cei:graphic url=\"http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0067.jpg\" /> </cei:figure> <cei:physicalDesc /> </cei:witness> </cei:witListPar> <cei:diplomaticAnalysis> <cei:listBiblRegest> <cei:bibl>Morel, Nr. 1.</cei:bibl> <cei:bibl>Regesta imperii II/1, 1, Nr. 157.</cei:bibl> <cei:bibl>Helbok, Regesten Vorarlberg, Nr. 132.</cei:bibl> <cei:bibl>UB Südl. St. Gallen, Band I, Nr. 67.</cei:bibl> <cei:bibl>Hidber, Urkundenregister, Band I, Nr. 1025.</cei:bibl> </cei:listBiblRegest> <cei:listBiblEdition> <cei:bibl>MGH DO I, Nr. 94.</cei:bibl> <cei:bibl>DAE, Band G, Nr. 25, S. 25.</cei:bibl> <cei:bibl>QW I/1, Nr. 28.</cei:bibl> <cei:bibl>Gfr, Band 43, 1888, S. 322f..</cei:bibl> </cei:listBiblEdition> <cei:listBiblErw> <cei:bibl>Sickel, Kaiserurkunden, S. 70, 72-77.</cei:bibl> <cei:bibl>MGH Ergänzungen, Nr. O.I.094.</cei:bibl> </cei:listBiblErw> </cei:diplomaticAnalysis> </cei:chDesc> </cei:body> </cei:text> </atom:content> </atom:entry>";
    private ExistResource resource;

    @BeforeClass
    public void setUp() throws Exception {
        resource = new ExistResource(NAME, PARENT_URI, XML_CONTENT);
    }

    @Test
    public void testConstructor() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getAtomId(), CHARTER_ATOM_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongResource() throws Exception {
        ExistResource wrongResource = new ExistResource(NAME, PARENT_URI, WRONG_XML_CONTENT);
        Charter charter = new Charter(wrongResource);
        assertEquals(charter.getAtomId(), CHARTER_ATOM_ID);
    }

    @Test
    public void testGetAtomId() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getAtomId(), CHARTER_ATOM_ID);
    }

    @Test
    public void testGetStatus() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getStatus(), STATUS);
    }
}