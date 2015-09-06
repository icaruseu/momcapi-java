package eu.icarus.momca.momcapi.model.xml.ead;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by djell on 06/09/2015.
 */
public abstract class DescriptiveElement extends Element {

    private static final String EAD_URI = Namespace.EAD.getUri();
    @NotNull
    private final Heading heading;
    @NotNull
    private final List<Paragraph> paragraphs = new ArrayList<>(0);

    public DescriptiveElement(@NotNull DescriptiveElementName elementName, @Nullable String heading, @NotNull String... paragraphs) {

        super(elementName.getQualifiedName(), EAD_URI);

        this.heading = createHeading(heading);
        this.paragraphs.addAll(createParagraphs(paragraphs));

        this.appendChild(this.heading);
        this.paragraphs.forEach(this::appendChild);

    }

    private Heading createHeading(@Nullable String heading) {
        return new Heading(heading);
    }

    private List<Paragraph> createParagraphs(@NotNull String... paragraphs) {

        List<Paragraph> paragraphList = new ArrayList<>();

        if (paragraphs.length == 0) {

            Paragraph emptyParagraph = new Paragraph("");
            paragraphList.add(emptyParagraph);

        } else {

            for (String paragraph : paragraphs) {

                Paragraph paragraphElement = new Paragraph(paragraph);
                paragraphList.add(paragraphElement);

            }
        }

        return paragraphList;

    }

    @NotNull
    public Heading getHeading() {
        return heading;
    }

    @NotNull
    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

}
