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


    // SPECTRUM 1 test data
    private static final String SPECTRUM_1_ID = "TEST-SPECTRUM-ID1";

    // SPECTRUM 2 test data
    private static final String SPECTRUM_2_ID = "TEST-SPECTRUM-ID2";

    // SPECTRUM 3 test data
    private static final String SPECTRUM_3_ID = "TEST-SPECTRUM-ID3";

    //Projects and assays
    private static final String PROJECT_1_ACCESSION = "PROJECT-1-ACCESSION";
    private static final String PROJECT_2_ACCESSION = "PROJECT-2-ACCESSION";
    private static final String ASSAY_1_1_ACCESSION = "ASSAY-1-1-ACCESSION";
    private static final String ASSAY_2_1_ACCESSION = "ASSAY-2-1-ACCESSION";
    private static final String ASSAY_2_2_ACCESSION = "ASSAY-2-2-ACCESSION";


    public static final long ZERO_DOCS = 0L;
    public static final long SINGLE_DOC = 1L;


    @Resource
    private SpectrumIndexService spectrumIndexService;

    @Resource
    private SpectrumSearchService spectrumSearchService;


    @Before
    public void setUp() throws Exception {
        // delete all data
        deleteAllData();
        // insert test data
        insertTestData();

    }

    @After
    public void tearDown() throws Exception {
        // delete all data
        deleteAllData();
    }

    private void deleteAllData() {
        spectrumIndexService.deleteAll();
    }

    private void insertTestData() {
        addSpectrum_1();
        addSpectrum_2();
        addSpectrum_3();
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


    private void addSpectrum_1() {
        Spectrum spectrum = new Spectrum();

        spectrum.setId(SPECTRUM_1_ID);
        spectrum.setProjectAccession(PROJECT_1_ACCESSION);
        spectrum.setAssayAccession(ASSAY_1_1_ACCESSION);

        spectrumIndexService.save(spectrum);
    }

    private void addSpectrum_2() {
        Spectrum spectrum = new Spectrum();

        spectrum.setId(SPECTRUM_2_ID);
        spectrum.setProjectAccession(PROJECT_2_ACCESSION);
        spectrum.setAssayAccession(ASSAY_2_1_ACCESSION);

        spectrumIndexService.save(spectrum);
    }

    private void addSpectrum_3() {
        Spectrum spectrum = new Spectrum();

        spectrum.setId(SPECTRUM_3_ID);
        spectrum.setProjectAccession(PROJECT_2_ACCESSION);
        spectrum.setAssayAccession(ASSAY_2_2_ACCESSION);

        spectrumIndexService.save(spectrum);

    }

}
