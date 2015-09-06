package eu.icarus.momca.momcapi.model.xml.ead;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by djell on 06/09/2015.
 */
public class Odd extends DescriptiveElement {

    public Odd() {
        this(null);
    }


    public Odd(@Nullable String heading, @NotNull String... paragraphs) {
        super(DescriptiveElementName.ODD, heading, paragraphs);
    }

}
