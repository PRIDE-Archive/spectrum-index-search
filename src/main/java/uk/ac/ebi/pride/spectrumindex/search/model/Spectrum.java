package uk.ac.ebi.pride.spectrumindex.search.model;

import org.apache.solr.client.solrj.beans.Field;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 */
public class Spectrum {

    @Field(SpectrumFields.ID)
    private String id;

    @Field(SpectrumFields.PROJECT_ACCESSION)
    private String projectAccession;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectAccession() {
        return projectAccession;
    }

    public void setProjectAccession(String projectAccession) {
        this.projectAccession = projectAccession;
    }
}
