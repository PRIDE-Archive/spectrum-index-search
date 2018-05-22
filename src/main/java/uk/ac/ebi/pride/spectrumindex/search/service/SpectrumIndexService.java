package uk.ac.ebi.pride.spectrumindex.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.MongoSpectrumRepository;

import javax.annotation.Resource;
import java.util.Collection;

/** Service to index and delete spectra. */
@Service
@Slf4j
public class SpectrumIndexService {

  @Resource private MongoSpectrumRepository mongoSpectrumRepository;

  /** Defauilt constructor. */
  public SpectrumIndexService() {}

  /**
   * Sets the spectra repository.
   *
   * @param mongoSpectrumRepository the spectra repository
   */
  public void setMongoSpectrumRepository(MongoSpectrumRepository mongoSpectrumRepository) {
    this.mongoSpectrumRepository = mongoSpectrumRepository;
  }

  /**
   * Saves a spectra to the repo.
   *
   * @param spectrum the spectra to save
   */
  @SuppressWarnings("WeakerAccess")
  public void save(Spectrum spectrum) {
    mongoSpectrumRepository.save(spectrum);
  }

  /**
   * Saves a collection of spectra to the repo.
   *
   * @param spectra the spectra to save
   */
  public void save(Collection<Spectrum> spectra) {
    if (CollectionUtils.isEmpty(spectra)) {
      log.error("No Spectrum to save");
    } else {
      debugSaveSpectrum(spectra);
      mongoSpectrumRepository.saveAll(spectra);
    }
  }

  /**
   * Deletes a spectrum from the repo.
   *
   * @param spectrum the spectra to be deleted
   */
  @SuppressWarnings("WeakerAccess")
  public void delete(Spectrum spectrum) {
    mongoSpectrumRepository.delete(spectrum);
  }

  /**
   * Deletes a collection of spectra from the repo.
   *
   * @param spectra the spectra to be deleted
   */
  public void delete(Collection<Spectrum> spectra) {
    if (CollectionUtils.isEmpty(spectra)) {
      log.error("No Spectra to delete");
    } else {
      mongoSpectrumRepository.deleteAll(spectra);
    }
  }

  /** Deletes all spectra in the repo. */
  @SuppressWarnings("WeakerAccess")
  public void deleteAll() {
    mongoSpectrumRepository.deleteAll();
  }

  /**
   * Outputs debug information when saving spectra
   *
   * @param spectra the spectra to output
   */
  private void debugSaveSpectrum(Iterable<Spectrum> spectra) {
    if (log.isDebugEnabled()) {
      int i = 0;
      for (Spectrum spectrum : spectra) {
        log.debug("Saving Spectra " + i + " with ID: " + spectrum.getId());
        log.debug("Project: " + spectrum.getProjectAccession());
        log.debug("Assay: " + spectrum.getAssayAccession());
        log.debug("Num PeaksI: " + spectrum.getPeaksIntensities().length);
        log.debug("Num PeaksM: " + spectrum.getPeaksMz().length);
        i++;
      }
    }
  }
}
