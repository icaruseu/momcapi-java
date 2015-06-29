package eu.icarus.momca.momcapi.resource;

/**
 * Created by daniel on 27.06.2015.
 */
public enum Namespace {

    APP("http://www.w3.org/2007/app"),
    ATOM("http://www.w3.org/2005/Atom"),
    CEI("http://www.monasterium.net/NS/cei"),
    EAD("urn:isbn:1-931666-22-9"),
    EAG("http://www.archivgut-online.de/eag"),
    XRX("http://www.monasterium.net/NS/xrx");

    private final String uri;

    Namespace(String uri) {
        this.uri = uri;
    }

    public String getPrefix() {
        return this.name().toLowerCase();
    }

    public String getUri() {
        return uri;
    }

}
