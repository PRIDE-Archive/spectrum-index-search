package uk.ac.ebi.pride.spectrumindex.search.util;


import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SpectrumJmzReaderMapper {

    public static uk.ac.ebi.pride.spectrumindex.search.model.Spectrum createSolrSpectrum(String projectAccession, String assayAccession, Ms2Query jmzReaderSpectrum) {
        Spectrum res = new Spectrum();

        res.setId(jmzReaderSpectrum.getTitle().substring(3)); // remove 'íd=' from the title
        res.setProjectAccession(projectAccession);
        res.setAssayAccession(assayAccession);
        if (jmzReaderSpectrum.getPrecursorCharge()!=null) res.setPrecursorCharge(jmzReaderSpectrum.getPrecursorCharge());
        if (jmzReaderSpectrum.getPrecursorMZ()!=null) res.setPrecursorMz(jmzReaderSpectrum.getPrecursorMZ());
        if (jmzReaderSpectrum.getPrecursorIntensity()!= null) res.setPrecursorIntensity(jmzReaderSpectrum.getPrecursorIntensity());
        if (jmzReaderSpectrum.getMsLevel()!= null) res.setMsLevel(jmzReaderSpectrum.getMsLevel());

        if (jmzReaderSpectrum.getPeakList()!=null && jmzReaderSpectrum.getPeakList()!= null) {
            double[] peaksIntensities = new double[jmzReaderSpectrum.getPeakList().size()];
            double[] peaksMz = new double[jmzReaderSpectrum.getPeakList().size()];
            int i = 0;
            for (Map.Entry<Double, Double> peakEntry : jmzReaderSpectrum.getPeakList().entrySet()) {
                peaksIntensities[i] = peakEntry.getValue();
                peaksMz[i] = peakEntry.getKey();
                i++;
            }
            res.setPeaksIntensities(peaksIntensities);
            res.setPeaksMz(peaksMz);
            res.setNumPeaks(peaksMz.length);
        }
        return res;
    }
}
