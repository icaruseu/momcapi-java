package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by djell on 10/09/2015.
 */
public class SourceDesc extends Element {

    public static final String CEI_URI = Namespace.CEI.getUri();
    private List<String> biblAbstract = new ArrayList<>(0);
    private List<String> biblTenor = new ArrayList<>(0);

    public SourceDesc() {
        super("cei:sourceDesc", CEI_URI);
    }

    public SourceDesc(@Nullable List<String> biblAbstract, @Nullable List<String> biblTenor) {

        this();

        if (biblAbstract != null) {
            this.biblAbstract = biblAbstract;
            appendBibElements("sourceDescRegest", biblAbstract);
        }

        if (biblTenor != null) {
            this.biblTenor = biblTenor;
            appendBibElements("sourceDescVolltext", biblTenor);
        }

    }

    private void appendBibElements(@NotNull String localElementName, @NotNull List<String> biblAbstract) {

        if (!localElementName.isEmpty() && !biblAbstract.isEmpty()) {

            Element newElement = new Element("cei:" + localElementName, CEI_URI);

            for (String bibl : biblAbstract) {

                Element biblElement = new Element("cei:bibl", CEI_URI);
                biblElement.appendChild(bibl);
                newElement.appendChild(biblElement);

            }

            this.appendChild(newElement);

        }

    }

    public List<String> getBiblAbstract() {
        return biblAbstract;
    }

    public List<String> getBiblTenor() {
        return biblTenor;
    }

}
