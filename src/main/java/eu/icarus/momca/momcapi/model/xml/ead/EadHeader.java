package eu.icarus.momca.momcapi.model.xml.ead;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;

/**
 * Created by djell on 06/09/2015.
 */
public class EadHeader extends Element {

    private static final String EAD_URI = Namespace.EAD.getUri();

    public EadHeader() {

        super("ead:eadheader", EAD_URI);

        appendChild(new Element("ead:eadid", EAD_URI));

        Element filedesc = new Element("ead:filedesc", EAD_URI);
        Element titlestmt = new Element("ead:titlestmt", EAD_URI);
        titlestmt.appendChild(new Element("ead:titleproper", EAD_URI));
        titlestmt.appendChild(new Element("ead:author", EAD_URI));
        filedesc.appendChild(titlestmt);
        appendChild(filedesc);

    }

}
