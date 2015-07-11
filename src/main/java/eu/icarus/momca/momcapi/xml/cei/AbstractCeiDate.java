package eu.icarus.momca.momcapi.xml.cei;

import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 10.07.2015.
 */
public abstract class AbstractCeiDate extends Element {

    private final String literalDate;

    AbstractCeiDate(@NotNull Element element, @NotNull String literalDate) {
        super(element);
        this.literalDate = literalDate;
        this.appendChild(literalDate);
    }

    public String getLiteralDate() {
        return literalDate;
    }

    public abstract boolean isValid();

    public abstract boolean isWrongDateType();

    @Override
    public String toString() {
        return "AbstractCeiDate{" +
                "literalDate='" + literalDate + '\'' +
                "} " + super.toString();
    }

}
