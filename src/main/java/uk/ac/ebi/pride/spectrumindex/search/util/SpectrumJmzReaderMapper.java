package uk.ac.ebi.pride.spectrumindex.search.util;


import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectrumJmzReaderMapper {


    private static final String ENTRY_SEPARATOR = " ";

    public static uk.ac.ebi.pride.spectrumindex.search.model.Spectrum createSolrSpectrum(String projectAccession, String assayAccession, uk.ac.ebi.pride.tools.jmzreader.model.Spectrum jmzReaderSpectrum) {
        Spectrum res = new Spectrum();

        res.setId(jmzReaderSpectrum.getId());
        res.setProjectAccession(projectAccession);
        res.setAssayAccession(assayAccession);
        res.setPrecursorCharge(jmzReaderSpectrum.getPrecursorCharge());
        res.setPrecursorMz(jmzReaderSpectrum.getPrecursorMZ());
        res.setPrecursorIntensity(jmzReaderSpectrum.getPrecursorIntensity());

        double[] peaksIntensities = new double[jmzReaderSpectrum.getPeakList().size()];
        double[] peaksMz = new double[jmzReaderSpectrum.getPeakList().size()];
        int i = 0;
        for (Map.Entry<Double, Double> peakEntry: jmzReaderSpectrum.getPeakList().entrySet()) {
            peaksIntensities[i] = peakEntry.getValue();
            peaksMz[i]= peakEntry.getKey();
            i++;
        }
        res.setPeaksIntensities(peaksIntensities);
        res.setPeaksMz(peaksMz);
        return res;
    }
}
