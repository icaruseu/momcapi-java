package eu.icarus.momca.momcapi.existquery;

/**
 * Created by Daniel on 07.03.2015.
 */
public class ExistQuery {

    private final String query;

    ExistQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return query;
    }

}
