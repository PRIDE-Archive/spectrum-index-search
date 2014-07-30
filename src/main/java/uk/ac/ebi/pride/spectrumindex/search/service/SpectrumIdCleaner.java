package uk.ac.ebi.pride.spectrumindex.search.service;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 */
public class SpectrumIdCleaner {

    public static String getCleanSpectrumId(String spectrumId) {
        return spectrumId.replaceAll("[;=\\.]","_");
    }

}
