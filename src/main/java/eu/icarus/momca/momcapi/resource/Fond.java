package eu.icarus.momca.momcapi.resource;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Fond {

    private final FondEad fondEad;
    private final FondPreferences fondPreferences;

    public Fond(FondEad fondEad, FondPreferences fondPreferences) {
        this.fondEad = fondEad;
        this.fondPreferences = fondPreferences;
    }

    public FondEad getFondEad() {
        return fondEad;
    }

    public FondPreferences getFondPreferences() {
        return fondPreferences;
    }

}
