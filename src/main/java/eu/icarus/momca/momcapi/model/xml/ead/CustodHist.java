package eu.icarus.momca.momcapi.model.xml.ead;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by djell on 06/09/2015.
 */
public class CustodHist extends DescriptiveElement {

    public CustodHist() {
        this(null);
    }


    public CustodHist(@Nullable String heading, @NotNull String... paragraphs) {
        super(DescriptiveElementName.CUSTODHIST, heading, paragraphs);
    }

}
