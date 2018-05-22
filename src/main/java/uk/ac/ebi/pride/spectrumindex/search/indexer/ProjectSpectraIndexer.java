package uk.ac.ebi.pride.spectrumindex.search.indexer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.util.SpectrumJmzReaderMapper;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

import javax.annotation.Resource;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@SuppressWarnings("WeakerAccess")
public class ProjectSpectraIndexer {

  private static final int MAX_PAGE_SIZE = 1000;
  @Resource private SpectrumIndexService spectrumIndexService;
  @Resource private SpectrumSearchService spectrumSearchService;
  private int indexingSizeStep;

  public ProjectSpectraIndexer(
      SpectrumIndexService spectrumIndexService,
      SpectrumSearchService spectrumSearchService,
      int indexingSizeStep) {
    this.spectrumIndexService = spectrumIndexService;
    this.spectrumSearchService = spectrumSearchService;
    this.indexingSizeStep = indexingSizeStep;
  }

  public void indexAllSpectraForProjectAndAssay(
      String projectAccession, String assayAccession, File mgfFile) {
    Instant start = Instant.now();
    try {
      MgfFile inputParser = new MgfFile(mgfFile);
      List<Spectrum> spectraToIndex = new LinkedList<>();
      log.info("There are " + inputParser.getSpectraCount() + " spectra to index");
      log.info("Spectra index size step: " + indexingSizeStep);
      for (int i = 1; i <= inputParser.getSpectraCount(); i++) {
        Ms2Query spectrum = (Ms2Query) inputParser.getSpectrumByIndex(i);
        Spectrum mongoSpectrum =
            SpectrumJmzReaderMapper.createMongoSpectrum(projectAccession, assayAccession, spectrum);
        spectraToIndex.add(mongoSpectrum);
        if (indexingSizeStep <= spectraToIndex.size()) {
          this.spectrumIndexService.save(spectraToIndex);
          spectraToIndex = new LinkedList<>();
        }
      }
      if (!CollectionUtils.isEmpty(spectraToIndex)) { // Finally... the last bit
        this.spectrumIndexService.save(spectraToIndex);
      }
    } catch (Exception e) {
      log.error("Problems parsing mgf file", e);
    }
    log.info(
        "DONE indexing all spectra for assay "
            + assayAccession
            + " in project "
            + projectAccession
            + " in "
            + Duration.between(start, Instant.now()).getSeconds()
            + " seconds");
  }

  /**
   * Deletes all PSMs for a project by project accession.
   *
   * @param projectAccession the project's accession number to delete PSMs
   */
  public void deleteAllSpectraForProject(String projectAccession) {
    long spectraCount = spectrumSearchService.countByProjectAccession(projectAccession);
    List<Spectrum> initialSpectraFound;
    while (0 < spectraCount) {
      for (int i = 0; i < (spectraCount / MAX_PAGE_SIZE) + 1; i++) {
        initialSpectraFound =
            spectrumSearchService
                .findByProjectAccession(projectAccession, PageRequest.of(i, MAX_PAGE_SIZE))
                .getContent();
        spectrumIndexService.delete(initialSpectraFound);
      }
      spectraCount = spectrumSearchService.countByProjectAccession(projectAccession);
    }
  }

  /**
   * Deletes all PSMs for a project by assay accession.
   *
   * @param assayAccession the assay number to delete PSMs
   */
  public void deleteAllSpectraForAssay(String assayAccession) {
    long spectraCount = spectrumSearchService.countByAssayAccession(assayAccession);
    List<Spectrum> initialSpectraFound;
    while (0 < spectraCount) {
      for (int i = 0; i < (spectraCount / MAX_PAGE_SIZE) + 1; i++) {
        initialSpectraFound =
            spectrumSearchService
                .findByAssayAccession(assayAccession, PageRequest.of(i, MAX_PAGE_SIZE))
                .getContent();
        spectrumIndexService.delete(initialSpectraFound);
      }
      spectraCount = spectrumSearchService.countByAssayAccession(assayAccession);
    }
  }

  public void setIndexingSizeStep(int indexingSizeStep) {
    this.indexingSizeStep = indexingSizeStep;
  }
}
