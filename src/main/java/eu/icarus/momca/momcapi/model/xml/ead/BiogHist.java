package eu.icarus.momca.momcapi.model.xml.ead;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by djell on 06/09/2015.
 */
public class BiogHist extends DescriptiveElement {

    public BiogHist() {
        this(null);
    }

    public BiogHist(@Nullable String heading, @NotNull String... paragraphs) {
        super(DescriptiveElementName.BIOGHIST, heading, paragraphs);
    }

}
