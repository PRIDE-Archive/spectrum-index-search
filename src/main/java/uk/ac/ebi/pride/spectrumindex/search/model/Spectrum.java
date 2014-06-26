package uk.ac.ebi.pride.spectrumindex.search.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

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

    @Field(SpectrumFields.ASSAY_ACCESSION)
    private String assayAccession;

    @Field(SpectrumFields.PRECURSOR_CHARGE)
    private int precursorCharge;

    @Field(SpectrumFields.IONS)
    private List<String> ions;

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

    public String getAssayAccession() {
        return assayAccession;
    }

    public void setAssayAccession(String assayAccession) {
        this.assayAccession = assayAccession;
    }

    public int getPrecursorCharge() {
        return precursorCharge;
    }

    public void setPrecursorCharge(int precursorCharge) {
        this.precursorCharge = precursorCharge;
    }

}
