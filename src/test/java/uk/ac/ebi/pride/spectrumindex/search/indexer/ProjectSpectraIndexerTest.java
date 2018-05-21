package uk.ac.ebi.pride.spectrumindex.search.indexer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.pride.spectrumindex.search.config.MongoTestConfiguration;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.MongoSpectrumRepository;

import javax.annotation.Resource;
import java.io.File;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoTestConfiguration.class})
public class ProjectSpectraIndexerTest {

  private static final String PATH_TO_MGF =
      "src/test/resources/submissions/PXD000021/PRIDE_Exp_Complete_Ac_27179.pride.mgf";
  private static final String PATH_TO_PROBLEMS_MGF =
      "src/test/resources/submissions/PXD000021/PRIDE_Exp_Complete_Ac_27180_problems.pride.mgf";
  private static final int NUM_PEAKS_SPECTRUM_1 = 269;
  private static final double PRECURSOR_MZ = 412.76431;
  private static final double PRECURSOR_INTENSITY = 0.0;
  private static final int PRECURSOR_CHARGE = 2;
  private static final String SPECTRUM_1_ID =
      "PXD000021;PRIDE_Exp_Complete_Ac_27179.xml;spectrum=0";
  private static final String PROJECT_1_ACCESSION = "PXD000021";
  private static final String PROJECT_1_ASSAY_1 = "27179";
  private static final String PROJECT_1_ASSAY_2 = "27180";
  private static final String SPLASH = "splash10-0udi-9000000000-547b773a253acd1da5bb";
  private static final int TEN_SECONDS = 10000;
  private static final int HITS = 4883;
  private ProjectSpectraIndexer projectSpectraIndexer;

  @Resource private SpectrumIndexService spectrumIndexService;
  @Resource private SpectrumSearchService spectrumSearchService;
  @Resource private MongoSpectrumRepository mongoSpectrumRepository;

  @Before
  public void setup() {
    projectSpectraIndexer =
        new ProjectSpectraIndexer(spectrumIndexService, spectrumSearchService, 100);
    projectSpectraIndexer.setIndexingSizeStep(150);
    spectrumIndexService.setMongoSpectrumRepository(mongoSpectrumRepository);
    spectrumSearchService.setMongoSpectrumRepository(mongoSpectrumRepository);
  }

  @Test
  public void testIndexMgf() throws Exception {
    indexMgf();
    testDeleteByProject(PROJECT_1_ACCESSION);
    indexMgf();
    testDeleteByAssay(PROJECT_1_ASSAY_1);
  }

  private void searchAndCheckByProjectAndAssay(int hits) {
    assertEquals(hits, (long) spectrumSearchService.countByProjectAccession(PROJECT_1_ACCESSION));
    assertEquals(hits, (long) spectrumSearchService.countByAssayAccession(PROJECT_1_ASSAY_1));
  }

  private void indexMgf() throws Exception {
    projectSpectraIndexer.indexAllSpectraForProjectAndAssay(
        PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1, new File(PATH_TO_MGF));
    Spectrum firstSpectrum = spectrumSearchService.findById(SPECTRUM_1_ID);
    assertEquals(PROJECT_1_ACCESSION, firstSpectrum.getProjectAccession());
    assertEquals(PROJECT_1_ASSAY_1, firstSpectrum.getAssayAccession());
    assertEquals(NUM_PEAKS_SPECTRUM_1, firstSpectrum.getPeaksMz().length);
    assertEquals(NUM_PEAKS_SPECTRUM_1, firstSpectrum.getPeaksIntensities().length);
    assertEquals(PRECURSOR_MZ, firstSpectrum.getPrecursorMz(), 0.0);
    assertEquals(PRECURSOR_INTENSITY, firstSpectrum.getPrecursorIntensity(), 25674.3);
    assertEquals(PRECURSOR_CHARGE, firstSpectrum.getPrecursorCharge());
    assertEquals(SPLASH, firstSpectrum.getSplash());
    Thread.sleep(TEN_SECONDS);
    searchAndCheckByProjectAndAssay(HITS);
  }

  private void testDeleteByProject(@SuppressWarnings("SameParameterValue") String projectAccession)
      throws Exception {
    projectSpectraIndexer.deleteAllSpectraForProject(projectAccession);
    Thread.sleep(TEN_SECONDS);
    searchAndCheckByProjectAndAssay(0);
  }

  private void testDeleteByAssay(@SuppressWarnings("SameParameterValue") String assayASccession)
      throws Exception {
    projectSpectraIndexer.deleteAllSpectraForAssay(assayASccession);
    Thread.sleep(TEN_SECONDS);
    searchAndCheckByProjectAndAssay(0);
  }

  @Test
  public void testProblematicMgf() {
    projectSpectraIndexer.indexAllSpectraForProjectAndAssay(
        PROJECT_1_ACCESSION, PROJECT_1_ASSAY_2, new File(PATH_TO_PROBLEMS_MGF));
  }
}
