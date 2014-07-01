package uk.ac.ebi.pride.spectrumindex.search.util;


import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;

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

        // TODO: build a proper ID
        // ...

        res.setProjectAccession(projectAccession);
        res.setAssayAccession(assayAccession);
        res.setPrecursorCharge(jmzReaderSpectrum.getPrecursorCharge());

        List<String> peaks = new LinkedList<String>();
        for (Map.Entry<Double, Double> peakEntry: jmzReaderSpectrum.getPeakList().entrySet()) {
            String peak = peakEntry.getKey() + ENTRY_SEPARATOR + peakEntry.getValue();
            peaks.add(peak);

        }
        res.setPeaks(peaks);
        return res;
    }
}
