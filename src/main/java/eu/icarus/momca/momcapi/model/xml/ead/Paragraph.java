package eu.icarus.momca.momcapi.model.xml.ead;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import nu.xom.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 06/09/2015.
 */
public class Paragraph extends Element {

    @NotNull
    private final Optional<String> content;

    public Paragraph(@Nullable String content) {

        super("ead:p", Namespace.EAD.getUri());

        if (content == null || content.isEmpty()) {

            this.content = Optional.empty();

        } else {

            this.content = Optional.of(content);

            Element contentElement = Util.parseToElement("<ead:p xmlns:ead='urn:isbn:1-931666-22-9'>" + content + "</ead:p>");

            List<Node> nodes = new ArrayList<>();
            for (int i = 0; i < contentElement.getChildCount(); i++) {
                Node node = contentElement.getChild(i).copy();
                nodes.add(node);
            }

            nodes.forEach(this::appendChild);

        }

    }

    @NotNull
    public Optional<String> getContent() {
        return content;
    }

}
