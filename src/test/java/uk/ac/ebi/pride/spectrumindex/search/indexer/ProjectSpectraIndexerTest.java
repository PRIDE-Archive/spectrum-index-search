package uk.ac.ebi.pride.spectrumindex.search.indexer;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.SolrSpectrumRepository;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.SolrSpectrumRepositoryFactory;

import java.io.File;
import java.util.List;

public class ProjectSpectraIndexerTest extends SolrTestCaseJ4 {


    private static final String PATH_TO_MGF = "src/test/resources/submissions/PXD000021/PRIDE_Exp_Complete_Ac_27179.pride.mgf";
    private static final int NUM_PEAKS_SPECTRUM_1 = 269;
    private static final double FIRST_PEAK_MZ = 160.028;
    private static final double FIRST_PEAK_INTENSITY = 1.082;
    private static Logger logger = LoggerFactory.getLogger(ProjectSpectraIndexerTest.class);

    private static final int NUM_RESULTS_PER_PAGE = 100;
    private static final String SPECTRUM_1_ID = "PXD000021;PRIDE_Exp_Complete_Ac_27179.xml;spectrum=0";
//    private static final String SPECTRUM_1_ID = "id_PXD000021_PRIDE_Exp_Complete_Ac_27179_xml_spectrum_0";
    private static final String PROJECT_1_ACCESSION = "PXD000021";
    private static final String PROJECT_1_ASSAY_1 = "27179";


    private SolrSpectrumRepositoryFactory solrSpectrumRepositoryFactory;

    private ProjectSpectraIndexer projectSpectraIndexer;
    private SpectrumIndexService spectrumIndexService;
    private SpectrumSearchService spectrumSearchService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        SolrServer server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
        solrSpectrumRepositoryFactory = new SolrSpectrumRepositoryFactory(new SolrTemplate(server));
        SolrSpectrumRepository solrSpectrumRepository = solrSpectrumRepositoryFactory.create();
        spectrumSearchService = new SpectrumSearchService(solrSpectrumRepository);
        spectrumIndexService = new SpectrumIndexService(solrSpectrumRepository, server);
        projectSpectraIndexer =
                new ProjectSpectraIndexer(
                        spectrumSearchService,
                        spectrumIndexService,
                        100
                );

    }

    @BeforeClass
    public static void initialise() throws Exception {
        initCore("src/test/resources/solr/collection1/conf/solrconfig.xml",
                "src/test/resources/solr/collection1/conf/schema.xml",
                "src/test/resources/solr");



    }

    @Test
    public void testIndexMgf() throws Exception {

        projectSpectraIndexer.indexAllSpectraForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1, new File(PATH_TO_MGF));

        List<Spectrum> res = spectrumSearchService.findById(SPECTRUM_1_ID);
        assertEquals(1, res.size());

        Spectrum firstSpectrum = res.get(0);

        assertEquals(PROJECT_1_ACCESSION, firstSpectrum.getProjectAccession());
        assertEquals(PROJECT_1_ASSAY_1, firstSpectrum.getAssayAccession());
        assertEquals(NUM_PEAKS_SPECTRUM_1, firstSpectrum.getPeaksMz().length);
        assertEquals(NUM_PEAKS_SPECTRUM_1, firstSpectrum.getPeaksIntensities().length);
        assertTrue(FIRST_PEAK_MZ==firstSpectrum.getPeaksMz()[0]);
        assertTrue(FIRST_PEAK_INTENSITY==firstSpectrum.getPeaksIntensities()[0]);

    }

}
