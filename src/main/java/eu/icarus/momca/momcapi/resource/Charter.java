package eu.icarus.momca.momcapi.resource;


import eu.icarus.momca.momcapi.exist.MetadataCollectionName;
import eu.icarus.momca.momcapi.resource.atom.AtomAuthor;
import eu.icarus.momca.momcapi.resource.atom.CharterAtomId;
import eu.icarus.momca.momcapi.resource.cei.CeiFigure;
import eu.icarus.momca.momcapi.resource.cei.CeiIdno;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 25.06.2015.
 */
public class Charter extends ExistResource {

    @NotNull
    private final AtomAuthor atomAuthor;
    @NotNull
    private final CharterAtomId atomId;
    @NotNull
    private final CeiIdno ceiIdno;
    @NotNull
    private final List<CeiFigure> ceiWitnessOrigFigures;
    @NotNull
    private final CharterStatus status;

    public Charter(@NotNull ExistResource existResource) {

        super(existResource);

        this.status = initStatus();

        this.atomId = initCharterAtomId();
        this.atomAuthor = new AtomAuthor(queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL));
        this.ceiIdno = new CeiIdno(queryUniqueElement(XpathQuery.QUERY_CEI_IDNO_ID), queryUniqueElement(XpathQuery.QUERY_CEI_IDNO_TEXT));

        this.ceiWitnessOrigFigures = new ArrayList<>(initCeiWitnessOrigFigures());

    }

    @NotNull
    public AtomAuthor getAtomAuthor() {
        return atomAuthor;
    }

    @NotNull
    public CharterAtomId getAtomId() {
        return atomId;
    }

    @NotNull
    public CeiIdno getCeiIdno() {
        return ceiIdno;
    }

    @NotNull
    public List<CeiFigure> getCeiWitnessOrigFigures() {
        return ceiWitnessOrigFigures;
    }

    @NotNull
    public CharterStatus getStatus() {
        return status;
    }

    @NotNull
    @Override
    public String toString() {
        return "Charter{" +
                "atomId=" + atomId +
                ", status=" + status +
                "} " + super.toString();
    }

    @NotNull
    private List<CeiFigure> initCeiWitnessOrigFigures() {

        List<CeiFigure> results = new ArrayList<>(0);

        Nodes figures = listQueryResultNodes(XpathQuery.QUERY_CEI_WITNESS_ORIG_FIGURE);

        for (int i = 0; i < figures.size(); i++) {

            Element figure = (Element) figures.get(i);
            String n = figure.getAttribute("n") == null ? "" : figure.getAttribute("n").getValue();
            Elements childElements = figure.getChildElements("graphic", Namespace.CEI.getUri());

            switch (childElements.size()) {

                case 0:
                    break;

                case 1:
                    Element graphic = childElements.get(0);
                    String url = graphic.getAttribute("url") == null ? "" : childElements.get(0).getAttribute("url").getValue();
                    String text = graphic.getValue();
                    results.add(new CeiFigure(n, url, text));
                    break;

                default:
                    throw new IllegalArgumentException("More than one child-elements of 'cei:figure'. Only one allowed, 'cei:graphic'.");

            }

        }

        return results;

    }

    @NotNull
    private CharterAtomId initCharterAtomId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);
        if (idString.isEmpty()) {
            throw new IllegalArgumentException("No atom:id in charter.");
        } else {
            return new CharterAtomId(idString);
        }

    }

    private CharterStatus initStatus() {

        CharterStatus status;

        if (getParentUri().contains(MetadataCollectionName.METADATA_CHARTER_IMPORT.getValue())) {
            status = CharterStatus.IMPORTED;
        } else if (getParentUri().contains(MetadataCollectionName.XRX_USER.getValue())) {
            status = CharterStatus.PRIVATE;
        } else if (getParentUri().contains(MetadataCollectionName.METADATA_CHARTER_SAVED.getValue())) {
            status = CharterStatus.SAVED;
        } else {
            status = CharterStatus.PUBLIC;
        }

        return status;

    }

    @NotNull
    private String queryUniqueElement(@NotNull XpathQuery query) {

        List<String> atomQueryResults = listQueryResultStrings(query);

        String result;

        switch (atomQueryResults.size()) {
            case 0:
                result = "";
                break;
            case 1:
                result = atomQueryResults.get(0);
                break;
            default:
                throw new IllegalArgumentException(String.format("More than one results for Query '%s'", query.getQuery()));

        }

        return result;

    }

}
