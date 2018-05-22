package uk.ac.ebi.pride.spectrumindex.search.util;

import edu.ucdavis.fiehnlab.spectra.hash.core.types.SpectraType;
import edu.ucdavis.fiehnlab.spectra.hash.core.util.SplashUtil;
import org.springframework.util.CollectionUtils;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

import java.util.Iterator;
import java.util.SortedMap;

public class SpectrumJmzReaderMapper {

  public static uk.ac.ebi.pride.spectrumindex.search.model.Spectrum createMongoSpectrum(
      String projectAccession, String assayAccession, Ms2Query jmzReaderSpectrum) {
    Spectrum result = new Spectrum();
    StringBuilder stringBuilder = new StringBuilder();
    result.setId(jmzReaderSpectrum.getTitle().substring(3)); // remove 'íd=' from the title
    result.setProjectAccession(projectAccession);
    result.setAssayAccession(assayAccession);
    if (jmzReaderSpectrum.getPrecursorCharge() != null) {
      result.setPrecursorCharge(jmzReaderSpectrum.getPrecursorCharge());
    }
    if (jmzReaderSpectrum.getPrecursorMZ() != null) {
      result.setPrecursorMz(jmzReaderSpectrum.getPrecursorMZ());
    }
    if (jmzReaderSpectrum.getPrecursorIntensity() != null) {
      result.setPrecursorIntensity(jmzReaderSpectrum.getPrecursorIntensity());
    }
    if (jmzReaderSpectrum.getMsLevel() != null) {
      result.setMsLevel(jmzReaderSpectrum.getMsLevel());
    }
    if (!CollectionUtils.isEmpty(jmzReaderSpectrum.getPeakList())) {
      double[] peaksIntensities = new double[jmzReaderSpectrum.getPeakList().size()];
      double[] peaksMz = new double[jmzReaderSpectrum.getPeakList().size()];
      // This for will build the string that will be need it to generate the splash
      Iterator<SortedMap.Entry<Double, Double>> peakLists =
          jmzReaderSpectrum.getPeakList().entrySet().iterator();
      SortedMap.Entry<Double, Double> peakEntry;
      for (int i = 0; peakLists.hasNext(); i++) {
        peakEntry = peakLists.next();
        peaksIntensities[i] = peakEntry.getValue();
        stringBuilder.append(peaksIntensities[i]);
        stringBuilder.append(":");
        peaksMz[i] = peakEntry.getKey();
        stringBuilder.append(peaksMz[i]);
        stringBuilder.append(" ");
      }
      result.setPeaksIntensities(peaksIntensities);
      result.setPeaksMz(peaksMz);
      result.setNumPeaks(peaksMz.length);
      result.setSplash(SplashUtil.splash(stringBuilder.toString(), SpectraType.MS));
    }
    return result;
  }

  public static String getCleanSpectrumId(String spectrumId) {
    return spectrumId.replaceAll("[;=.]", "_");
  }
}
