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
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.SolrSpectrumRepositoryFactory;

public class ProjectSpectraIndexerTest extends SolrTestCaseJ4 {


    private static Logger logger = LoggerFactory.getLogger(ProjectSpectraIndexerTest.class);

    private static final String PROJECT_1_ACCESSION = "PXD000581";
    private static final String PROJECT_2_ACCESSION = "TST000121";

    private static final String PROJECT_1_ASSAY_1 = "32411";
    private static final String PROJECT_1_ASSAY_2 = "32416";
    private static final String PROJECT_2_ASSAY_1 = "00001";

    private SolrSpectrumRepositoryFactory solrSpectrumRepositoryFactory;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        SolrServer server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
        solrSpectrumRepositoryFactory = new SolrSpectrumRepositoryFactory(new SolrTemplate(server));
    }

    @BeforeClass
    public static void initialise() throws Exception {
        initCore("src/test/resources/solr/collection1/conf/solrconfig.xml",
                "src/test/resources/solr/collection1/conf/schema.xml",
                "src/test/resources/solr");

    }

    @Test
    public void testIndexAllPsmsForProjectAndAssay() throws Exception {
        SpectrumSearchService SpectrumSearchService = new SpectrumSearchService(this.solrSpectrumRepositoryFactory.create());
        SpectrumIndexService SpectrumIndexService = new SpectrumIndexService(this.solrSpectrumRepositoryFactory.create());

        ProjectSpectraIndexer projectSpectraIndexer = new ProjectSpectraIndexer(SpectrumSearchService, SpectrumIndexService);

        projectSpectraIndexer.indexAllSpectraForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1);
        projectSpectraIndexer.indexAllSpectraForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_2);
        projectSpectraIndexer.indexAllSpectraForProjectAndAssay(PROJECT_2_ACCESSION, PROJECT_2_ASSAY_1);

        // TODO: actual checks for this test case
    }

    @Test
    public void testDeleteAllPsmsForProject() throws Exception {

        SpectrumSearchService SpectrumSearchService = new SpectrumSearchService(this.solrSpectrumRepositoryFactory.create());
        SpectrumIndexService SpectrumIndexService = new SpectrumIndexService(this.solrSpectrumRepositoryFactory.create());

        ProjectSpectraIndexer projectSpectraIndexer = new ProjectSpectraIndexer(SpectrumSearchService, SpectrumIndexService);

        projectSpectraIndexer.indexAllSpectraForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1);
        projectSpectraIndexer.indexAllSpectraForProjectAndAssay(PROJECT_1_ACCESSION, PROJECT_1_ASSAY_2);


        // TODO: actual checks for this test case

    }
}
