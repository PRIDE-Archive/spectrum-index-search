package uk.ac.ebi.pride.spectrumindex.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.MongoSpectrumRepository;

import javax.annotation.Resource;
import java.util.Collection;

@Service
@Slf4j
public class SpectrumIndexService {

  @Resource private MongoSpectrumRepository mongoSpectrumRepository;

  public SpectrumIndexService() {}

  public void setMongoSpectrumRepository(MongoSpectrumRepository mongoSpectrumRepository) {
    this.mongoSpectrumRepository = mongoSpectrumRepository;
  }

  @Transactional
  public void save(Spectrum spectrum) {
    mongoSpectrumRepository.save(spectrum);
  }

  @Transactional
  public void save(Collection<Spectrum> spectra) {
    if (CollectionUtils.isEmpty(spectra)) {
      log.error("No Spectrum to save");
    } else {
      debugSaveSpectrum(spectra);
      mongoSpectrumRepository.saveAll(spectra);
    }
  }

  @Transactional
  public void delete(Spectrum spectrum) {
    mongoSpectrumRepository.delete(spectrum);
  }

  @Transactional
  public void delete(Collection<Spectrum> spectra) {
    if (CollectionUtils.isEmpty(spectra)) {
      log.error("No Spectra to delete");
    } else {
      mongoSpectrumRepository.deleteAll(spectra);
    }
  }

  @Transactional
  public void deleteAll() {
    mongoSpectrumRepository.deleteAll();
  }

  private void debugSaveSpectrum(Iterable<Spectrum> spectra) {
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
