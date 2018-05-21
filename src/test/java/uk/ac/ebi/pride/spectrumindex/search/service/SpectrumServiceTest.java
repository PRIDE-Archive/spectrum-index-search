package uk.ac.ebi.pride.spectrumindex.search.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.pride.spectrumindex.search.config.MongoTestConfiguration;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.util.SpectrumJmzReaderMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoTestConfiguration.class})
@Slf4j
public class SpectrumServiceTest {

  private static final String SPECTRUM_1_ID = "TEST-SPECTRUM-ID1";
  private static final String SPECTRUM_2_ID = "TEST-SPECTRUM-ID2";
  private static final String SPECTRUM_3_ID = "TEST-SPECTRUM-ID3";

  private static final String PROJECT_1_ACCESSION = "PROJECT-1-ACCESSION";
  private static final String PROJECT_2_ACCESSION = "PROJECT-2-ACCESSION";
  private static final String ASSAY_1_1_ACCESSION = "ASSAY-1-1-ACCESSION";
  private static final String ASSAY_2_1_ACCESSION = "ASSAY-2-1-ACCESSION";
  private static final String ASSAY_2_2_ACCESSION = "ASSAY-2-2-ACCESSION";

  @Resource private SpectrumIndexService spectrumIndexService;
  @Resource private SpectrumSearchService spectrumSearchService;

  @Before
  public void setUp() {
    deleteAllData();
    insertTestData();
  }

  @After
  public void tearDown() {
    deleteAllData();
  }

  private void deleteAllData() {
    spectrumIndexService.deleteAll();
  }

  private void insertTestData() {
    Spectrum spectrum_one = new Spectrum();
    spectrum_one.setId(SPECTRUM_1_ID);
    spectrum_one.setProjectAccession(PROJECT_1_ACCESSION);
    spectrum_one.setAssayAccession(ASSAY_1_1_ACCESSION);
    spectrumIndexService.save(spectrum_one);
    Spectrum spectrum_two = new Spectrum();
    spectrum_two.setId(SPECTRUM_2_ID);
    spectrum_two.setProjectAccession(PROJECT_2_ACCESSION);
    spectrum_two.setAssayAccession(ASSAY_2_1_ACCESSION);
    spectrumIndexService.save(spectrum_two);
    Spectrum spectrum_three = new Spectrum();
    spectrum_three.setId(SPECTRUM_3_ID);
    spectrum_three.setProjectAccession(PROJECT_2_ACCESSION);
    spectrum_three.setAssayAccession(ASSAY_2_2_ACCESSION);
    spectrumIndexService.save(spectrum_three);
  }

  @Test
  public void testSearchById() {
    Spectrum spectrum = spectrumSearchService.findById(SPECTRUM_1_ID);
    log.info("Cleaned example ID: " + SpectrumJmzReaderMapper.getCleanSpectrumId(spectrum.getId()));
    assertNotNull(spectrum);
    assertEquals(SPECTRUM_1_ID, spectrum.getId());
    spectrumIndexService.delete(spectrum);
    assertEquals(spectrumSearchService.findById(SPECTRUM_1_ID), new Spectrum());
    spectrumIndexService.save(new ArrayList<>());
    List<String> projectAccessionsToSearch = new ArrayList<>();
    projectAccessionsToSearch.add(PROJECT_1_ACCESSION);
    List<String> assayAccessionsToSearch = new ArrayList<>();
    projectAccessionsToSearch.add(ASSAY_1_1_ACCESSION);
    assertEquals(
        0,
        spectrumSearchService
            .findByProjectAccession(projectAccessionsToSearch, PageRequest.of(0, 10))
            .getContent()
            .size());
    assertEquals(
        0,
        spectrumSearchService
            .findByAssayAccession(assayAccessionsToSearch, PageRequest.of(0, 10))
            .getContent()
            .size());
    spectrum.setMsLevel(1);
    assertEquals(1, spectrum.getMsLevel(), 1);
    spectrum.setRetentionTime(new ArrayList<>());
    assertEquals(0, spectrum.getRetentionTime().size());
    spectrum.setIdentifiedSpectra(true);
    assertTrue(spectrum.isIdentifiedSpectra());
    spectrum.setNumPeaks(1);
    assertEquals(1, spectrum.getNumPeaks());
    log.info("Spectrum hash code:" + spectrum.hashCode());
  }

  @Test
  public void testCountProjectAccession() {
    assertEquals(1, (long) spectrumSearchService.countByProjectAccession(PROJECT_1_ACCESSION));
    assertEquals(2, (long) spectrumSearchService.countByProjectAccession(PROJECT_2_ACCESSION));
  }

  @Test
  public void testCountAssayAccession() {
    assertEquals(1, (long) spectrumSearchService.countByAssayAccession(ASSAY_1_1_ACCESSION));
    assertEquals(1, (long) spectrumSearchService.countByAssayAccession(ASSAY_2_1_ACCESSION));
    assertEquals(1, (long) spectrumSearchService.countByAssayAccession(ASSAY_2_2_ACCESSION));
  }
}
// todo JavaDoc
// todo review warnings