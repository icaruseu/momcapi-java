package eu.icarus.momca.momcapi.query;


/**
 * A xQuery to be used to query the eXist database for MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 11.07.2015.
 */
public class ExistQuery {

    private final String query;

    /**
     * Instantiates a new Exist query.
     *
     * @param query the query
     */
    ExistQuery(String query) {
        this.query = query;
    }

    /**
     * Gets query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }
}
