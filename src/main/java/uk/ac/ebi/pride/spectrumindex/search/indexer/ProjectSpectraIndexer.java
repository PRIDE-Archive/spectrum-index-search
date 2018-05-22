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

/**
 * Indexes and deletes spectra for PRIDE-generated MGF files.
 */
@Slf4j
@SuppressWarnings("WeakerAccess")
public class ProjectSpectraIndexer {

  private static final int MAX_PAGE_SIZE = 1000;
  @Resource private SpectrumIndexService spectrumIndexService;
  @Resource private SpectrumSearchService spectrumSearchService;
  private int indexingStepSize;

  /**
   * Constructor, sets the spectrum index and search services, and specifies what size chunks to
   * process in.
   *
   * @param spectrumIndexService the spectrum index service
   * @param spectrumSearchService the spectrum search service
   * @param indexingStepSize the index step size
   */
  public ProjectSpectraIndexer(
      SpectrumIndexService spectrumIndexService,
      SpectrumSearchService spectrumSearchService,
      int indexingStepSize) {
    this.spectrumIndexService = spectrumIndexService;
    this.spectrumSearchService = spectrumSearchService;
    this.indexingStepSize = 0 < indexingStepSize ? indexingStepSize : 1000; // can't be < 0
  }

  /**
   * Indexes all spectra for a project's assay
   *
   * @param projectAccession the project accession
   * @param assayAccession the assay accession
   * @param mgfFile the (PRIDE) MGF to index
   */
  public void indexAllSpectraForProjectAndAssay(
      String projectAccession, String assayAccession, File mgfFile) {
    Instant start = Instant.now();
    try {
      MgfFile inputParser = new MgfFile(mgfFile);
      List<Spectrum> spectraToIndex = new LinkedList<>();
      log.info("There are " + inputParser.getSpectraCount() + " spectra to index");
      log.info("Spectra index step size: " + indexingStepSize);
      for (int i = 1; i <= inputParser.getSpectraCount(); i++) {
        Ms2Query spectrum = (Ms2Query) inputParser.getSpectrumByIndex(i);
        Spectrum mongoSpectrum =
            SpectrumJmzReaderMapper.createMongoSpectrum(projectAccession, assayAccession, spectrum);
        spectraToIndex.add(mongoSpectrum);
        if (indexingStepSize <= spectraToIndex.size()) {
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
   * Deletes all spectra for a project by project accession.
   *
   * @param projectAccession the project's accession number
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
   * Deletes all spectra for a project by assay accession.
   *
   * @param assayAccession the assay number
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

  /**
   * Sets the indexing step size.
   *
   * @param indexingStepSize the indexing step size
   */
  public void setIndexingStepSize(int indexingStepSize) {
    this.indexingStepSize = indexingStepSize;
  }
}
