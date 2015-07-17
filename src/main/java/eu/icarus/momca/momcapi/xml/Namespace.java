package eu.icarus.momca.momcapi.xml;

/**
 * A namespace used in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 27.06.2015.
 */
public enum Namespace {

    APP("http://www.w3.org/2007/app"),
    ATOM("http://www.w3.org/2005/Atom"),
    CEI("http://www.monasterium.net/NS/cei"),
    CONFIG("http://exist-db.org/Configuration"),
    EAD("urn:isbn:1-931666-22-9"),
    EAG("http://www.archivgut-online.de/eag"),
    EAP("http://www.monasterium.net/NS/eap"),
    XRX("http://www.monasterium.net/NS/xrx");

    private final String uri;

    Namespace(String uri) {
        this.uri = uri;
    }

    /**
     * @return The prefix for the namespace, e.g. {@code cei}.
     */
    public String getPrefix() {
        return this.name().toLowerCase();
    }

    /**
     * @return The URI defined for the namespace, e.g. {@code "http://www.monasterium.net/NS/cei"}.
     */
    public String getUri() {
        return uri;
    }

}
