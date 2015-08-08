package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 08/08/2015.
 */
public class Entry extends Element {

    public Entry(@NotNull Id id, @NotNull Author author, @NotNull String currentDateTime, @NotNull Element childContent) {
        super("atom:entry", Namespace.ATOM.getUri());
        initContent(author, currentDateTime, id, childContent);
    }

    private void initContent(@NotNull Author author, @NotNull String currentDateTime, @NotNull Id id, @NotNull Element childContent) {

        String atomUri = Namespace.ATOM.getUri();
        String appUri = Namespace.APP.getUri();

        Element atomId = new Element("atom:id", atomUri);
        atomId.appendChild(id);
        appendChild(atomId);

        appendChild(new Element("atom:title", atomUri));

        Element atomPublished = new Element("atom:published", atomUri);
        atomPublished.appendChild(currentDateTime);
        appendChild(atomPublished);
        Element atomUpdated = new Element("atom:updated", atomUri);
        atomUpdated.appendChild(currentDateTime);
        appendChild(atomUpdated);

        appendChild(author);

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
