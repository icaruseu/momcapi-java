package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import eu.icarus.momca.momcapi.xml.cei.Figure;
import eu.icarus.momca.momcapi.xml.cei.Idno;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by daniel on 27.06.2015.
 */
public class CharterTest {


    private static final AtomAuthor ATOM_AUTHOR = new AtomAuthor("user1.testuser@dev.monasterium.net");
    @NotNull
    private static final Idno IDNO = new Idno("KAE_Urkunde_Nr_1", "KAE, Urkunde Nr. 1");
    @NotNull
    private static final IdCharter ID_CHARTER = new IdCharter(new AtomId("tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1"));
    @NotNull
    private static final String INVALID_XML_CONTENT = "<atom:entry xmlns:atom='http://www.w3.org/2005/Atom'> <atom:id>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1</atom:id> <atom:title /> <atom:published>2011-11-28T09:57:23.704+01:00</atom:published> <atom:updated>2011-11-28T09:57:23.704+01:00</atom:updated> <atom:author> <atom:email>user1.testuser@dev.monasterium.net</atom:email> </atom:author> <app:control xmlns:app='http://www.w3.org/2007/app'> <app:draft>no</app:draft> </app:control> <atom:content type='application/xml'> <cei:text id='1' n='1' type='charter' xmlns='http://www.monasterium.net/NS/cei' xmlns:cei='http://www.monasterium.net/NS/cei'> <cei:front> <cei:sourceDesc> <cei:sourceDescRegest> <cei:bibl>QW I/1, Nr. 28</cei:bibl> </cei:sourceDescRegest> </cei:sourceDesc> </cei:front> <cei:body> <cei:idno id='KAE_Urkunde_Nr_1' old='1'>KAE, Urkunde Nr. 1</cei:idno> <cei:chDesc> <cei:abstract>König Otto I. verleiht auf Bitte Herzog Hermanns dem Kloster Meinradszell (Einsiedeln), das samt einer Kirche vom jetzigen <cei:persName>Abt Eberhard</cei:persName>auf Boden, der dem Herzog von einigen Getreuen zu eigen gegeben worden war, mit dessen Unterstützung errichtet worden ist, das Recht freier Wahl des Abtes nach dem Tode Eberhards und Immunität.</cei:abstract> <cei:issued> <cei:placeName>Frankfurt</cei:placeName> <cei:date value='09471027'>0947-10-27</cei:date> </cei:issued> <cei:witnessOrig n='A'> <cei:archIdentifier> <cei:arch>Klosterarchiv Einsiedeln</cei:arch> <cei:idno>KAE, A.BI.1</cei:idno> </cei:archIdentifier> <cei:traditioForm>Original</cei:traditioForm> <cei:figure n=\"KAE_A_BI_1-v\"> <cei:graphic url='KAE_A_BI_1-v.jpg' >KAE_A_BI_1-v</cei:graphic> </cei:figure> <cei:figure> <cei:graphic url='KAE_A_BI_1-r.jpg' /> </cei:figure> <cei:auth> <cei:sealDesc>1 Siegel</cei:sealDesc> </cei:auth> </cei:witnessOrig> <cei:witListPar> <cei:witness n='B'> <cei:archIdentifier> <cei:arch>Klosterarchiv Einsiedeln</cei:arch> <cei:idno>KAE, A.II.1</cei:idno> </cei:archIdentifier> <cei:traditioForm>Kopie</cei:traditioForm> <cei:figure> <cei:graphic url='http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0067.jpg' /> </cei:figure> <cei:physicalDesc /> </cei:witness> </cei:witListPar> <cei:diplomaticAnalysis> <cei:listBiblRegest> <cei:bibl>Morel, Nr. 1.</cei:bibl> <cei:bibl>Regesta imperii II/1, 1, Nr. 157.</cei:bibl> <cei:bibl>Helbok, Regesten Vorarlberg, Nr. 132.</cei:bibl> <cei:bibl>UB Südl. St. Gallen, Band I, Nr. 67.</cei:bibl> <cei:bibl>Hidber, Urkundenregister, Band I, Nr. 1025.</cei:bibl> </cei:listBiblRegest> <cei:listBiblEdition> <cei:bibl>MGH DO I, Nr. 94.</cei:bibl> <cei:bibl>DAE, Band G, Nr. 25, S. 25.</cei:bibl> <cei:bibl>QW I/1, Nr. 28.</cei:bibl> <cei:bibl>Gfr, Band 43, 1888, S. 322f..</cei:bibl> </cei:listBiblEdition> <cei:listBiblErw> <cei:bibl>Sickel, Kaiserurkunden, S. 70, 72-77.</cei:bibl> <cei:bibl>MGH Ergänzungen, Nr. O.I.094.</cei:bibl> </cei:listBiblErw> </cei:diplomaticAnalysis> </cei:chDesc> </cei:body> </cei:text> </atom:content> </atom:entry>";
    @NotNull
    private static final String NAME = "KAE_Urkunde_Nr_1.cei.xml";
    @NotNull
    private static final String PARENT_URI = "/db/mom-data/metadata.charter.public/CH-KAE/Urkunden";
    @NotNull
    private static final CharterStatus STATUS = CharterStatus.PUBLIC;
    @NotNull
    private static final String WRONG_XML_CONTENT = "<atom:entry xmlns:atom='http://www.w3.org/2005/Atom' />";
    @NotNull
    private static final String XML_CONTENT = "<atom:entry xmlns:atom='http://www.w3.org/2005/Atom'> <atom:id>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1</atom:id> <atom:title /> <atom:published>2011-11-28T09:57:23.704+01:00</atom:published> <atom:updated>2011-11-28T09:57:23.704+01:00</atom:updated> <atom:author> <atom:email>user1.testuser@dev.monasterium.net</atom:email> </atom:author> <app:control xmlns:app='http://www.w3.org/2007/app'> <app:draft>no</app:draft> </app:control> <atom:content type='application/xml'> <cei:text id='1' n='1' type='charter' xmlns='http://www.monasterium.net/NS/cei' xmlns:cei='http://www.monasterium.net/NS/cei'> <cei:front> <cei:sourceDesc> <cei:sourceDescRegest> <cei:bibl>QW I/1, Nr. 28</cei:bibl> </cei:sourceDescRegest> </cei:sourceDesc> </cei:front> <cei:body> <cei:idno id='KAE_Urkunde_Nr_1' old='1'>KAE, Urkunde Nr. 1</cei:idno> <cei:chDesc> <cei:abstract>König Otto I. verleiht auf Bitte Herzog Hermanns dem Kloster Meinradszell (Einsiedeln), das samt einer Kirche vom jetzigen <cei:persName>Abt Eberhard</cei:persName>auf Boden, der dem Herzog von einigen Getreuen zu eigen gegeben worden war, mit dessen Unterstützung errichtet worden ist, das Recht freier Wahl des Abtes nach dem Tode Eberhards und Immunität.</cei:abstract> <cei:issued> <cei:placeName>Frankfurt</cei:placeName> <cei:date value='12471027'>1247-10-27</cei:date> </cei:issued> <cei:witnessOrig n='A'> <cei:archIdentifier> <cei:arch>Klosterarchiv Einsiedeln</cei:arch> <cei:idno>KAE, A.BI.1</cei:idno> </cei:archIdentifier> <cei:traditioForm>Original</cei:traditioForm> <cei:figure n=\"KAE_A_BI_1-v\"> <cei:graphic url='KAE_A_BI_1-v.jpg' >KAE_A_BI_1-v</cei:graphic> </cei:figure> <cei:figure> <cei:graphic url='KAE_A_BI_1-r.jpg' /> </cei:figure> <cei:auth> <cei:sealDesc>1 Siegel</cei:sealDesc> </cei:auth> </cei:witnessOrig> <cei:witListPar> <cei:witness n='B'> <cei:archIdentifier> <cei:arch>Klosterarchiv Einsiedeln</cei:arch> <cei:idno>KAE, A.II.1</cei:idno> </cei:archIdentifier> <cei:traditioForm>Kopie</cei:traditioForm> <cei:figure> <cei:graphic url='http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0067.jpg' /> </cei:figure> <cei:physicalDesc /> </cei:witness> </cei:witListPar> <cei:diplomaticAnalysis> <cei:listBiblRegest> <cei:bibl>Morel, Nr. 1.</cei:bibl> <cei:bibl>Regesta imperii II/1, 1, Nr. 157.</cei:bibl> <cei:bibl>Helbok, Regesten Vorarlberg, Nr. 132.</cei:bibl> <cei:bibl>UB Südl. St. Gallen, Band I, Nr. 67.</cei:bibl> <cei:bibl>Hidber, Urkundenregister, Band I, Nr. 1025.</cei:bibl> </cei:listBiblRegest> <cei:listBiblEdition> <cei:bibl>MGH DO I, Nr. 94.</cei:bibl> <cei:bibl>DAE, Band G, Nr. 25, S. 25.</cei:bibl> <cei:bibl>QW I/1, Nr. 28.</cei:bibl> <cei:bibl>Gfr, Band 43, 1888, S. 322f..</cei:bibl> </cei:listBiblEdition> <cei:listBiblErw> <cei:bibl>Sickel, Kaiserurkunden, S. 70, 72-77.</cei:bibl> <cei:bibl>MGH Ergänzungen, Nr. O.I.094.</cei:bibl> </cei:listBiblErw> </cei:diplomaticAnalysis> </cei:chDesc> </cei:body> </cei:text> </atom:content> </atom:entry>";
    @NotNull
    private static final String XML_CONTENT_DATERANGE = "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"> <atom:id>tag:www.monasterium.net,2011:/charter/MedDocBulgEmp/1202-xx-xx_Belota</atom:id> <atom:title /> <atom:published>2015-06-29T18:12:14.06+02:00</atom:published> <atom:updated>2015-06-29T18:12:14.06+02:00</atom:updated> <atom:author> <atom:email>admin</atom:email> </atom:author> <app:control xmlns:app=\"http://www.w3.org/2007/app\"> <app:draft>no</app:draft> </app:control> <atom:content type=\"application/xml\"> <cei:text type=\"charter\" xmlns:cei=\"http://www.monasterium.net/NS/cei\"> <cei:front /> <cei:body> <cei:idno id=\"1202-xx-xx_Belota\">1202-xx-xx_Belota</cei:idno> <cei:chDesc> <cei:abstract /> <cei:issued> <cei:placeName /> <cei:dateRange from=\"12020101\" to=\"12021231\">During 1202</cei:dateRange> </cei:issued> <cei:witnessOrig /> <cei:witListPar /> <cei:diplomaticAnalysis> </cei:diplomaticAnalysis> <cei:lang_MOM>Latin.</cei:lang_MOM> </cei:chDesc> </cei:body> <cei:back /> </cei:text> </atom:content> </atom:entry>";
    private MomcaResource resource;

    @BeforeClass
    public void setUp() throws Exception {
        resource = new MomcaResource(NAME, PARENT_URI, XML_CONTENT);
    }

    @Test
    public void testConstructor() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getId().getContentXml().toXML(), ID_CHARTER.getContentXml().toXML());
    }

    @Test
    public void testConstructorWithInvalidCei() throws Exception {
        Charter charter = new Charter(new MomcaResource(NAME, PARENT_URI, INVALID_XML_CONTENT));
        assertEquals(charter.getValidationProblems().size(), 2);
        assertEquals(charter.getValidationProblems().get(0).getMessage(), "cvc-pattern-valid: Value '09471027' is not facet-valid with respect to pattern '-?[129]?[0-9][0-9][0-9][019][0-9][01239][0-9]' for type 'normalizedDateValue'.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithNonCei() throws Exception {
        new Charter(new MomcaResource(NAME, PARENT_URI, WRONG_XML_CONTENT));
    }

    @Test
    public void testGetAuthor() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getAuthor().get().getIdentifier(), ATOM_AUTHOR.getEmail());
    }

    @Test
    public void testGetCeiDate() throws Exception {

        Charter charterWithDate = new Charter(resource);
        assertEquals(charterWithDate.getDate().get().toXML(), "<cei:date xmlns:cei=\"http://www.monasterium.net/NS/cei\" value=\"12471027\">1247-10-27</cei:date>");

        Charter charterWithDateRange = new Charter(new MomcaResource(NAME, PARENT_URI, XML_CONTENT_DATERANGE));
        assertEquals(charterWithDateRange.getDate().get().toXML(), "<cei:dateRange xmlns:cei=\"http://www.monasterium.net/NS/cei\" from=\"12020101\" to=\"12021231\">During 1202</cei:dateRange>");

    }

    @Test
    public void testGetCeiIdno() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getIdno().toXML(), "<cei:idno xmlns:cei=\"http://www.monasterium.net/NS/cei\" id=\"KAE_Urkunde_Nr_1\">KAE, Urkunde Nr. 1</cei:idno>");
    }

    @Test
    public void testGetCeiWitnessOrigFigures() throws Exception {

        Charter charter = new Charter(resource);
        List<Figure> figures = charter.getFigures();
        assertEquals(figures.size(), 2);
        assertEquals(figures.get(0).toXML(), "<cei:figure xmlns:cei=\"http://www.monasterium.net/NS/cei\" n=\"KAE_A_BI_1-v\"><cei:graphic url=\"KAE_A_BI_1-v.jpg\">KAE_A_BI_1-v</cei:graphic></cei:figure>");
        assertEquals(figures.get(1).toXML(), "<cei:figure xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:graphic url=\"KAE_A_BI_1-r.jpg\" /></cei:figure>");

    }

    @Test
    public void testGetId() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getId().getContentXml().toXML(), ID_CHARTER.getContentXml().toXML());
    }

    @Test
    public void testGetIdno() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getIdno().toXML(), IDNO.toXML());
    }

    @Test
    public void testGetStatus() throws Exception {
        Charter charter = new Charter(resource);
        assertEquals(charter.getStatus(), STATUS);
    }

    @Test
    public void testGetValidationProblems() throws Exception {
        Charter invalidCharter = new Charter(new MomcaResource(NAME, PARENT_URI, INVALID_XML_CONTENT));
        assertEquals(invalidCharter.getValidationProblems().size(), 2);
        assertEquals(invalidCharter.getValidationProblems().get(0).getMessage(), "cvc-pattern-valid: Value '09471027' is not facet-valid with respect to pattern '-?[129]?[0-9][0-9][0-9][019][0-9][01239][0-9]' for type 'normalizedDateValue'.");
    }

    @Test
    public void testIsValidCei() throws Exception {

        Charter charter = new Charter(resource);
        assertTrue(charter.isValidCei());

        Charter invalidCharter = new Charter(new MomcaResource(NAME, PARENT_URI, INVALID_XML_CONTENT));
        assertFalse(invalidCharter.isValidCei());

    }

}