package eu.icarus.momca.momcapi.model.xml.cei;

import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 12/09/2015.
 */
public class Tenor extends MixedContentElement {

    Tenor(@NotNull String content) {
        super(content, "tenor");
    }

}
