package eu.icarus.momca.momcapi.exist;

/**
 * Created by daniel on 11.07.2015.
 */
public class ExistQuery {

    private final String query;

    ExistQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
