package eu.icarus.momca.momcapi.model.xml.ead;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 06/09/2015.
 */
public class ParagraphTest {


    public static final String EAD_CLOSE_TAG = "</ead:p>";
    public static final String EAD_OPEN_TAG = "<ead:p xmlns:ead=\"urn:isbn:1-931666-22-9\">";

    @NotNull
    private String getCorrectXml(String text) {
        return EAD_OPEN_TAG + text + EAD_CLOSE_TAG;
    }

    @Test
    public void testElementParagraph() throws Exception {

        String elementParagraph = "Le fotografie, per quanto preziose e utilissime, non bastano. Hanno bisogno di dati di corredo. Per questo motivo il software MOM CA, presente su questo portale e che consente di inserire dati che lo stesso software codifica in linguaggio XML, ha reso disponibili, a corredo delle foto, dati utili riguardanti i documenti, finora presenti solo in alcuni contributi a stampa, ma che ora vengono resi fruibili grazie al motore di ricerca (http://monasterium.net/mom/search). Ci riferiamo alle date croniche, alle collocazioni e ai riferimenti bibliografici ad alcuni regesti ottocenteschi. <ead:list><ead:listhead>Per inserire questi dati abbiamo utilizzato due pubblicazioni: </ead:listhead><ead:item>l'Inventario del fondo di Stefano Palmieri, già comparso a stampa e revisionato nel 2010, disponibile ora online in formato pdf (http://www.storiapatrianapoli.it/getFile.php?id=17). Da esso abbiamo tratto i dati iniziali per realizzare la struttura del nostro archivio digitale: date topiche e collocazioni.</ead:item><ead:item>I regesti della documentazione dei secoli X-XIII, comparsi in B. CAPASSO - R. BEVERE – G. DE BLASIIS - N. PARISIO, Elenco delle pergamene già appartenenti alla famiglia Fusco ed ora acquisite dalla Società Napoletana di Storia Patria, in «Archivio storico per le province napoletane», 8 (1883), pp. 153 - 161, 332 - 338, 775 - 787; 12 (1887), pp. 156 - 164, 436 - 448, 705 - 709, 823 - 835; 13 (1888), 161 - 172; 14 (1889), pp. 144 - 158, 353 - 373, 758 - 772; 15 (1890), pp. 654 - 661; 16 (1891), pp. 665 - 671; 18 (1893), pp. 538 - 555). Da questa pubblicazione abbiamo tratto l'indicazione bibliografica del regesto, mettendolo in relazione in MOM CA con il documento a cui esso corrisponde.</ead:item></ead:list>";

        Paragraph paragraph = new Paragraph(elementParagraph);

        assertTrue(paragraph.getContent().isPresent());
        assertEquals(paragraph.getContent().get(), elementParagraph);
        assertEquals(paragraph.toXML(), getCorrectXml(elementParagraph));

    }

    @Test
    public void testEmptyParagraph() throws Exception {

        String emptyParagraph = "";

        Paragraph paragraph = new Paragraph(emptyParagraph);

        assertFalse(paragraph.getContent().isPresent());
        assertEquals(paragraph.toXML(), "<ead:p xmlns:ead=\"urn:isbn:1-931666-22-9\" />");

    }

    @Test
    public void testTextParagraph() throws Exception {

        String textParagraph = "Dieses Selekt umfasst nur mehr die Reste eines vormals sehr umfangreichen Bestandes. Wertvollere Stücke wurden bereits nach der Säkularisation an die Wiener Zentralbehörden abgegeben und befinden sich heute im Haus-, Hof- und Staatsarchiv. Diese sind allerdings durch Publikationen (Salzburger Urkundenbuch, Bd. I-IV) großteils erschlossen.";

        Paragraph paragraph = new Paragraph(textParagraph);

        assertTrue(paragraph.getContent().isPresent());
        assertEquals(paragraph.getContent().get(), textParagraph);
        assertEquals(paragraph.toXML(), getCorrectXml(textParagraph));

    }

}