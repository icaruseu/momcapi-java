package eu.icarus.momca.momcapi.model.xml.atom;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 08/08/2015.
 */
public class AtomEntry extends Element {

    public AtomEntry(@NotNull AtomId atomId, @NotNull AtomAuthor atomAuthor, @NotNull String publishedDateTime,
                     @NotNull String updatedDateTime, @NotNull Element childContent) {
        super("atom:entry", Namespace.ATOM.getUri());
        initContent(atomAuthor, publishedDateTime, updatedDateTime, atomId, childContent);
    }

    private void initContent(@NotNull AtomAuthor atomAuthor, @NotNull String publishedDateTime, @NotNull String updatedDateTime,
                             @NotNull AtomId atomId, @NotNull Element childContent) {

        String atomUri = Namespace.ATOM.getUri();
        String appUri = Namespace.APP.getUri();

        appendChild(atomId.copy());

        appendChild(new Element("atom:title", atomUri));

        Element atomPublished = new Element("atom:published", atomUri);
        atomPublished.appendChild(publishedDateTime);
        appendChild(atomPublished);

        Element atomUpdated = new Element("atom:updated", atomUri);
        atomUpdated.appendChild(updatedDateTime);
        appendChild(atomUpdated);

        appendChild(atomAuthor);

        Element appControl = new Element("app:control", appUri);
        Element appDraft = new Element("app:draft", appUri);
        appDraft.appendChild("no");
        appControl.appendChild(appDraft);
        appendChild(appControl);

        Element atomContent = new Element("atom:content", atomUri);
        atomContent.addAttribute(new Attribute("type", "application/xml"));
        appendChild(atomContent);

        atomContent.appendChild(childContent);

    }

}
