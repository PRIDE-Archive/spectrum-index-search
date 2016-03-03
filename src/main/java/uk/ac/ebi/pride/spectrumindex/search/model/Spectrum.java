package uk.ac.ebi.pride.spectrumindex.search.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Document(collection = "spectra")
public class Spectrum {


    @Id
    private String id;

    private String projectAccession;

    private String assayAccession;

    private int precursorCharge;

    private double precursorMz;

    private double precursorIntensity;

    private int msLevel;

    private List<Long> retentionTime;

    private boolean identifiedSpectra;

    private double[] peaksIntensities;

    private double[] peaksMz;

    private int numPeaks;

    //Hash for spectrum
    private String splash;

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

    public int getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(int msLevel) {
        this.msLevel = msLevel;
    }

    public List<Long> getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(List<Long> retentionTime) {
        this.retentionTime = retentionTime;
    }

    public boolean isIdentifiedSpectra() {
        return identifiedSpectra;
    }

    public void setIdentifiedSpectra(boolean identifiedSpectra) {
        this.identifiedSpectra = identifiedSpectra;
    }

    public double[] getPeaksIntensities() {
        return this.peaksIntensities;
    }

    public void setPeaksIntensities(double[] peaksIntensities) {
        this.peaksIntensities = peaksIntensities;
    }

    public double[] getPeaksMz() {
        return this.peaksMz;
    }

    public void setPeaksMz(double[] peaksMz) {
        this.peaksMz = peaksMz;
    }

    public int getNumPeaks() {
        return numPeaks;
    }

    public void setNumPeaks(int numPeaks) {
        this.numPeaks = numPeaks;
    }

    //For the future. Maybe the splash can be use for the equals and hash if it is working
    public String getSplash() {
        return splash;
    }

    public void setSplash(String splash) {
        this.splash = splash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Spectrum)) return false;
        Spectrum spectrum = (Spectrum) o;
        return Objects.equals(id, spectrum.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
