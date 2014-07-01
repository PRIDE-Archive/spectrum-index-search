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

    @Field(SpectrumFields.PRECURSOR_MZ)
    private double precursorMz;

    @Field(SpectrumFields.PRECURSOR_INTENSITY)
    private double precursorIntensity;

    @Field(SpectrumFields.IDENTIFIED_SPECTRA)
    private boolean identifiedSpectra;

    @Field(SpectrumFields.PEAKS_INTENSITIES)
    private double[] peaksIntensities;

    @Field(SpectrumFields.PEAKS_MZ)
    private double[] peaksMz;

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

    public double getPrecursorMz() {
        return precursorMz;
    }

    public void setPrecursorMz(double precursorMz) {
        this.precursorMz = precursorMz;
    }

    public double getPrecursorIntensity() {
        return precursorIntensity;
    }

    public void setPrecursorIntensity(double precursorIntensity) {
        this.precursorIntensity = precursorIntensity;
    }

    public boolean isIdentifiedSpectra() {
        return identifiedSpectra;
    }

    public void setIdentifiedSpectra(boolean identifiedSpectra) {
        this.identifiedSpectra = identifiedSpectra;
    }

    public double[] getPeaksIntensities() {
        return peaksIntensities;
    }

    public void setPeaksIntensities(double[] peaksIntensities) {
        this.peaksIntensities = peaksIntensities;
    }

    public double[] getPeaksMz() {
        return peaksMz;
    }

    public void setPeaksMz(double[] peaksMz) {
        this.peaksMz = peaksMz;
    }
}
