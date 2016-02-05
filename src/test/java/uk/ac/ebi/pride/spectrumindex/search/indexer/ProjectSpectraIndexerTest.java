package uk.ac.ebi.pride.spectrumindex.search.indexer;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;

import javax.annotation.Resource;
import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-mongo-test-context.xml"})
public class ProjectSpectraIndexerTest {

    private static Logger logger = LoggerFactory.getLogger(ProjectSpectraIndexerTest.class);


    private static final String PATH_TO_MGF = "src/test/resources/submissions/PXD000021/PRIDE_Exp_Complete_Ac_27179.pride.mgf";
    private static final int NUM_PEAKS_SPECTRUM_1 = 269;
    private static final double FIRST_PEAK_MZ = 262.602;
    private static final double FIRST_PEAK_INTENSITY = 5.806;

    private static final String SPECTRUM_1_ID = "PXD000021;PRIDE_Exp_Complete_Ac_27179.xml;spectrum=0";
    private static final String PROJECT_1_ACCESSION = "PXD000021";
    private static final String PROJECT_1_ASSAY_1 = "27179";
    private ProjectSpectraIndexer projectSpectraIndexer;

    @Resource
    private SpectrumIndexService spectrumIndexService;
    @Resource
    private SpectrumSearchService spectrumSearchService;

    @Before
    public void setup() throws Exception {

        projectSpectraIndexer =
            new ProjectSpectraIndexer(
                    spectrumIndexService,
                    100
            );

    }


    @Test
    public void testIndexMgf() throws Exception {

        projectSpectraIndexer.indexAllSpectraForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1, new File(PATH_TO_MGF));
        //We force the commit for testing purposes (avoids wait four minutes)

        Spectrum firstSpectrum  = spectrumSearchService.findById(SPECTRUM_1_ID);

        assertEquals(PROJECT_1_ACCESSION, firstSpectrum.getProjectAccession());
        assertEquals(PROJECT_1_ASSAY_1, firstSpectrum.getAssayAccession());
        assertEquals(NUM_PEAKS_SPECTRUM_1, firstSpectrum.getPeaksMz().length);
        assertEquals(NUM_PEAKS_SPECTRUM_1, firstSpectrum.getPeaksIntensities().length);
        assertTrue(FIRST_PEAK_MZ==firstSpectrum.getPeaksMz()[0]);
        assertTrue(FIRST_PEAK_INTENSITY==firstSpectrum.getPeaksIntensities()[0]);

    }
}
