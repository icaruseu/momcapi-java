package eu.icarus.momca.momcapi.model.xml.eap;

import org.jetbrains.annotations.NotNull;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class EapSubdivision extends EapAbstract {

    public EapSubdivision(@NotNull String code, @NotNull String nativeForm) {
        super("subdivision", code, nativeForm);
    }

}
