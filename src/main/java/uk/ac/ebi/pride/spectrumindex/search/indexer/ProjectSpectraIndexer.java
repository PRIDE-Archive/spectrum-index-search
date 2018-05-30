package uk.ac.ebi.pride.spectrumindex.search.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.util.SpectrumJmzReaderMapper;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class ProjectSpectraIndexer {

  private static Logger logger = LoggerFactory.getLogger(ProjectSpectraIndexer.class.getName());

  private int indexingSizeStep;

  @Resource private SpectrumIndexService spectrumIndexService;
  @Resource private SpectrumSearchService spectrumSearchService;

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
    long startTime;
    long endTime;
    startTime = System.currentTimeMillis();
    try {
      MgfFile inputParser = new MgfFile(mgfFile);
      List<Spectrum> spectraToIndex = new LinkedList<Spectrum>();
      logger.info("There are " + inputParser.getSpectraCount() + " spectra to index");
      logger.info("Spectra index size step: " + indexingSizeStep);
      for (int i = 1; i <= inputParser.getSpectraCount(); i++) {
        Ms2Query spectrum = (Ms2Query) inputParser.getSpectrumByIndex(i);
        Spectrum mongoSpectrum =
            SpectrumJmzReaderMapper.createMongoSpectrum(projectAccession, assayAccession, spectrum);
        spectraToIndex.add(mongoSpectrum);
        if (spectraToIndex.size() >= indexingSizeStep) {
          this.spectrumIndexService.save(spectraToIndex);
          spectraToIndex = new LinkedList<Spectrum>();
        }
      }
      if (spectraToIndex.size() > 0) { // Finally... the last bit
        this.spectrumIndexService.save(spectraToIndex);
      }
    } catch (JMzReaderException e) {
      e.printStackTrace();
    }
    endTime = System.currentTimeMillis();
    logger.info(
        "DONE indexing all spectra for assay "
            + assayAccession
            + " in project "
            + projectAccession
            + " in "
            + (double) (endTime - startTime) / 1000.0
            + " seconds");
  }

  public void deleteAllSpectraForProject(String projectAccession) {
    int MAX_PAGE_SIZE = 1000;
    long spectraCount = spectrumSearchService.countByProjectAccession(projectAccession);
    List<Spectrum> initialSpectraFound;
    while (0 < spectraCount) {
      for (int i = 0; i < (spectraCount / MAX_PAGE_SIZE) + 1; i++) {
        initialSpectraFound =
            spectrumSearchService
                .findByProjectAccession(projectAccession, new PageRequest(i, MAX_PAGE_SIZE))
                .getContent();
        spectrumIndexService.delete(initialSpectraFound);
      }
      spectraCount = spectrumSearchService.countByProjectAccession(projectAccession);
    }
  }

  public void setIndexingSizeStep(int indexingSizeStep) {
    this.indexingSizeStep = indexingSizeStep;
  }
}
