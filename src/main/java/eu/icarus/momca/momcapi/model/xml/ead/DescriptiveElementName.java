package eu.icarus.momca.momcapi.model.xml.ead;

/**
 * Created by djell on 06/09/2015.
 */
public enum DescriptiveElementName {

    BIOGHIST("ead:bioghist"), CUSTODHIST("ead:custodhist"), ODD("ead:odd");

    private String qualifiedName;

    DescriptiveElementName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

}
