package uk.ac.ebi.pride.spectrumindex.search.service;

/**
 * @author Jose A. Dianes
 * @author ntoro
 * @version $Id$
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;

import javax.annotation.Resource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-mongo-test-context.xml")
public class SpectrumServiceTest {

    private static final String SPECTRUM_1_ID = "TEST-SPECTRUM-ID1";
    private static final String SPECTRUM_2_ID = "TEST-SPECTRUM-ID2";
    private static final String SPECTRUM_3_ID = "TEST-SPECTRUM-ID3";

    private static final String PROJECT_1_ACCESSION = "PROJECT-1-ACCESSION";
    private static final String PROJECT_2_ACCESSION = "PROJECT-2-ACCESSION";
    private static final String ASSAY_1_1_ACCESSION = "ASSAY-1-1-ACCESSION";
    private static final String ASSAY_2_1_ACCESSION = "ASSAY-2-1-ACCESSION";
    private static final String ASSAY_2_2_ACCESSION = "ASSAY-2-2-ACCESSION";

    @Resource
    private SpectrumIndexService spectrumIndexService;

    @Resource
    private SpectrumSearchService spectrumSearchService;

    @Before
    public void setUp() throws Exception {
        deleteAllData();
        insertTestData();
    }

    @After
    public void tearDown() throws Exception {
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
        assertNotNull(spectrum);
        assertEquals(SPECTRUM_1_ID, spectrum.getId());
    }

    @Test
    public void testCountProjectAccession() throws Exception {
        assertEquals((Long) 1L,  spectrumSearchService.countByProjectAccession(PROJECT_1_ACCESSION));
        assertEquals((Long) 2L,  spectrumSearchService.countByProjectAccession(PROJECT_2_ACCESSION));

    }

    @Test
    public void testCountAssayAccession() throws Exception {
        assertEquals((Long) 1L,  spectrumSearchService.countByAssayAccession(ASSAY_1_1_ACCESSION));
        assertEquals((Long) 1L,  spectrumSearchService.countByAssayAccession(ASSAY_2_1_ACCESSION));
        assertEquals((Long) 1L,  spectrumSearchService.countByAssayAccession(ASSAY_2_2_ACCESSION));

    }
}
