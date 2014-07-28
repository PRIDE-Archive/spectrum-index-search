package uk.ac.ebi.pride.spectrumindex.search.model;

import org.apache.solr.client.solrj.beans.Field;
import org.apache.commons.codec.binary.Base64;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 */
public class Spectrum {

    /**
     * Defines the number of bytes required in an UNENCODED byte array to hold
     * a dingle double value.
     */
    public static final int BYTES_TO_HOLD_DOUBLE = 8;

    @Field(SpectrumFields.ID)
    private String id;

    @Field(SpectrumFields.PROJECT_ACCESSION)
    private String projectAccession;

    @Field(SpectrumFields.ASSAY_ACCESSION)
    private String assayAccession;

    @Field(SpectrumFields.MS_LEVEL)
    private int msLevel;

    @Field(SpectrumFields.PRECURSOR_CHARGE)
    private int precursorCharge;

    @Field(SpectrumFields.PRECURSOR_MZ)
    private double precursorMz;

    @Field(SpectrumFields.PRECURSOR_INTENSITY)
    private double precursorIntensity;

    @Field(SpectrumFields.IDENTIFIED_SPECTRA)
    private boolean identifiedSpectra;

    @Field(SpectrumFields.PEAKS_INTENSITIES)
    private String peaksIntensities;

    @Field(SpectrumFields.PEAKS_MZ)
    private String peaksMz;

    @Field(SpectrumFields.RETENTION_TIME)
    private List<Long> retentionTime;

    public List<Long> getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(List<Long> retentionTime) {
        this.retentionTime = retentionTime;
    }

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
        return fromBytesStringToDoubles(this.peaksIntensities);
    }

    public void setPeaksIntensities(double[] peaksIntensities) {
        this.peaksIntensities = fromDoublesToBytesString(peaksIntensities);
    }

    public double[] getPeaksMz() {
       return fromBytesStringToDoubles(this.peaksMz);
    }

    public void setPeaksMz(double[] peaksMz) {
        this.peaksMz = fromDoublesToBytesString(peaksMz);
    }

    private double[] fromBytesStringToDoubles(String bytesString) {
        byte[] bytesArray = Base64.decodeBase64(bytesString);
        ByteBuffer bytes = ByteBuffer.wrap(bytesArray);
        bytes.order(ByteOrder.LITTLE_ENDIAN);
        double[] res = new double[bytesArray.length/BYTES_TO_HOLD_DOUBLE];
        int j=0;
        for (int i=0; i<bytesArray.length; i=i+BYTES_TO_HOLD_DOUBLE) {
            res[j] = bytes.getDouble(i);
            j++;
        }
        return res;
    }

    private byte[] fromDoublesToBytes(double[] doubles) {
        ByteBuffer buffer = ByteBuffer.allocate(doubles.length * BYTES_TO_HOLD_DOUBLE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (double aDouble: doubles) {
            buffer.putDouble(aDouble);
        }
        return buffer.array();
    }

    private double[] fromBytesToDoubles(byte[] bytesArray) {
        ByteBuffer bytes = ByteBuffer.wrap(bytesArray);
        bytes.order(ByteOrder.LITTLE_ENDIAN);
        double[] res = new double[bytesArray.length/BYTES_TO_HOLD_DOUBLE];
        int j=0;
        for (int i=0; i<bytesArray.length; i=i+BYTES_TO_HOLD_DOUBLE) {
            res[j] = bytes.getDouble(i);
            j++;
        }
        return res;
    }

    private String fromDoublesToBytesString(double[] doubles) {
        ByteBuffer buffer = ByteBuffer.allocate(doubles.length * BYTES_TO_HOLD_DOUBLE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (double aDouble: doubles) {
            buffer.putDouble(aDouble);
        }
        return Base64.encodeBase64String(buffer.array());
    }

    public void setMsLevel(int msLevel) {
        this.msLevel = msLevel;
    }

    public int getMsLevel() {
        return msLevel;
    }
}
