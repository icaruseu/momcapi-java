package eu.icarus.momca.momcapi.model.xml.xrx;

import eu.icarus.momca.momcapi.model.id.IdCharter;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 18/09/2015.
 */
public class Saved extends Element {

    public static final String URI = Namespace.XRX.getUri();
    @NotNull
    private String freigabe;
    @NotNull
    private IdCharter id;
    @NotNull
    private String startTime;

    public Saved(@NotNull IdCharter id, @NotNull String startTime, @NotNull String freigabe) {

        super("xrx:saved", URI);

        if (startTime.isEmpty() || (!freigabe.equals("yes") && !freigabe.equals("no"))) {
            throw new IllegalArgumentException("Illegal arguments!");
        }

        this.id = id;
        this.startTime = startTime;
        this.freigabe = freigabe;

        appendChild(createElement("id", id.getContentAsElement().getText()));
        appendChild(createElement("start_time", startTime));
        appendChild(createElement("freigabe", freigabe));

    }

    @NotNull
    private Element createElement(@NotNull String localizedName, @NotNull String content) {

        Element element = new Element("xrx:" + localizedName, Namespace.XRX.getUri());

        if (!content.isEmpty()) {
            element.appendChild(content);
        }

        return element;

    }

    @NotNull
    public String getFreigabe() {
        return freigabe;
    }

    @NotNull
    public IdCharter getId() {
        return id;
    }

    @NotNull
    public String getStartTime() {
        return startTime;
    }

}
