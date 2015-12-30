package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 23/08/2015.
 */
public class IdAnnotation extends IdAtomId {

    public IdAnnotation(@NotNull AtomId atomIdXml) {
        super(atomIdXml);
    }

    @Override
    public String toString() {
        return "IdAnnotation{" + getAtomId() + "}";
    }


}
