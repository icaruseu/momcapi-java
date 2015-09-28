package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.FigDesc;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Zone;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Figure extends Element {

    public static final String CEI_URI = Namespace.CEI.getUri();
    @NotNull
    private Optional<String> facs = Optional.empty();
    @NotNull
    private Optional<FigDesc> figDesc = Optional.empty();
    @NotNull
    private Optional<Graphic> graphic = Optional.empty();
    @NotNull
    private Optional<String> id = Optional.empty();
    @NotNull
    private Optional<String> n = Optional.empty();
    @NotNull
    private List<Zone> zones = new ArrayList<>(0);

    private Figure() {
        super("cei:figure", CEI_URI);
    }

    public Figure(@NotNull String url) {
        this();
        initChilds(null, new Graphic(url), null);
    }

    public Figure(@Nullable FigDesc figDesc, @Nullable Graphic graphic, @Nullable List<Zone> zones,
                  @NotNull String facs, @NotNull String id, @NotNull String n) {
        this();
        initChilds(figDesc, graphic, zones);
        initAttributes(facs, id, n);
    }

    public Figure(@NotNull Element figureElement) {

        this();

        Element figDescElement = figureElement.getFirstChildElement("figDesc", CEI_URI);
        FigDesc figDesc = (figDescElement == null) ? null : new FigDesc(figDescElement);

        Element graphicElement = figureElement.getFirstChildElement("graphic", CEI_URI);
        Graphic graphic = (graphicElement == null) ? null : new Graphic(graphicElement);

        Elements zoneElements = figureElement.getChildElements("zone", CEI_URI);
        List<Zone> zones = new ArrayList<>(0);
        for (int i = 0; i < zoneElements.size(); i++) {
            zones.add(new Zone(zoneElements.get(i)));
        }

        initChilds(figDesc, graphic, zones);

        String facs = figureElement.getAttributeValue("facs");
        String id = figureElement.getAttributeValue("id");
        String n = figureElement.getAttributeValue("n");

        initAttributes(facs, id, n);

    }

    @NotNull
    public Optional<String> getFacs() {
        return facs;
    }

    @NotNull
    public Optional<FigDesc> getFigDesc() {
        return figDesc;
    }

    @NotNull
    public Optional<Graphic> getGraphic() {
        return graphic;
    }

    @NotNull
    public Optional<String> getId() {
        return id;
    }

    @NotNull
    public Optional<String> getN() {
        return n;
    }

    @NotNull
    public String getUrl() {
        return graphic.map(Graphic::getUrl).orElse("");
    }

    @NotNull
    public List<Zone> getZones() {
        return zones;
    }

    private void initAttributes(@Nullable String facs, @Nullable String id, @Nullable String n) {

        if (facs != null && !facs.isEmpty()) {
            addAttribute(new Attribute("facs", facs));
            this.facs = Optional.of(facs);
        }

        if (id != null && !id.isEmpty()) {
            addAttribute(new Attribute("id", id));
            this.id = Optional.of(id);
        }

        if (n != null && !n.isEmpty()) {
            addAttribute(new Attribute("n", n));
            this.n = Optional.of(n);
        }

    }

    private void initChilds(@Nullable FigDesc figDesc, @Nullable Graphic graphic, @Nullable List<Zone> zones) {

        if (figDesc != null) {
            this.figDesc = Optional.of(figDesc);
            appendChild(figDesc);
        }

        if (graphic != null) {
            this.graphic = Optional.of(graphic);
            appendChild(graphic);
        }

        if (zones != null && !zones.isEmpty()) {
            this.zones = zones;
            zones.forEach(this::appendChild);
        }

    }

}
