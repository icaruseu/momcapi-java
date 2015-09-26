package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;

/**
 * Created by djell on 25/09/2015.
 */
public class Witness extends Element {


    public static final String CEI_URI = Namespace.CEI.getUri();

    public Witness() {
        super("cei:witness", CEI_URI);
    }

}
