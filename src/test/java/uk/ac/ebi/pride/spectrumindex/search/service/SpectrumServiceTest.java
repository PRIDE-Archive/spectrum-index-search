package uk.ac.ebi.pride.spectrumindex.search.service;

/**
 * @author Jose A. Dianes
 * @author ntoro
 * @version $Id$
 */

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.model.SpectrumFields;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.SolrSpectrumRepositoryFactory;

import java.util.List;


public class SpectrumServiceTest extends SolrTestCaseJ4 {


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

    private SolrServer server;
    private SolrSpectrumRepositoryFactory solrSpectrumRepositoryFactory;

    public static final long ZERO_DOCS = 0L;
    public static final long SINGLE_DOC = 1L;

    @BeforeClass
    public static void initialise() throws Exception {
        initCore("src/test/resources/solr/collection1/conf/solrconfig.xml",
                "src/test/resources/solr/collection1/conf/schema.xml",
                "src/test/resources/solr");
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());

        solrSpectrumRepositoryFactory = new SolrSpectrumRepositoryFactory(new SolrTemplate(server));

        // delete all data
        deleteAllData();
        // insert test data
        insertTestData();

        //We force the commit for testing purposes (avoids wait one minute)
        server.commit();
    }

    private void deleteAllData() {
        SpectrumIndexService spectrumIndexService = new SpectrumIndexService(solrSpectrumRepositoryFactory.create(), server);
        spectrumIndexService.deleteAll();
    }

    private void insertTestData() {
        addSpectrum_1();
        addSpectrum_2();
        addSpectrum_3();
    }

    @Test
    public void testThatNoResultsAreReturned() throws SolrServerException {
        SolrParams params = new SolrQuery("text that is not found");
        QueryResponse response = server.query(params);
        assertEquals(ZERO_DOCS, response.getResults().getNumFound());
    }

    @Test
    public void testSearchByAccessionUsingQuery() throws Exception {

        SolrParams params = new SolrQuery(SpectrumFields.ID + ":" + SPECTRUM_1_ID);
        QueryResponse response = server.query(params);
        assertEquals(SINGLE_DOC, response.getResults().getNumFound());
        assertEquals(SPECTRUM_1_ID, response.getResults().get(0).get(SpectrumFields.ID));

    }

    @Test
    public void testSearchById() {
        SpectrumSearchService spectrumSearchService = new SpectrumSearchService(solrSpectrumRepositoryFactory.create());

        List<Spectrum> spectrums = spectrumSearchService.findById(SPECTRUM_1_ID);

        assertNotNull(spectrums);
        assertEquals(1, spectrums.size());

        Spectrum spectrum1 = spectrums.get(0);
        assertEquals(SPECTRUM_1_ID, spectrum1.getId());
    }

    @Test
    public void testCountProjectAccession() throws Exception {
        SpectrumSearchService spectrumSearchService = new SpectrumSearchService(solrSpectrumRepositoryFactory.create());

        assertEquals((Long) 1L,  spectrumSearchService.countByProjectAccession(PROJECT_1_ACCESSION));
        assertEquals((Long) 2L,  spectrumSearchService.countByProjectAccession(PROJECT_2_ACCESSION));

    }

    @Test
    public void testCountAssayAccession() throws Exception {

        SpectrumSearchService spectrumSearchService = new SpectrumSearchService(solrSpectrumRepositoryFactory.create());

        assertEquals((Long) 1L,  spectrumSearchService.countByAssayAccession(ASSAY_1_1_ACCESSION));
        assertEquals((Long) 1L,  spectrumSearchService.countByAssayAccession(ASSAY_2_1_ACCESSION));
        assertEquals((Long) 1L,  spectrumSearchService.countByAssayAccession(ASSAY_2_2_ACCESSION));

    }


    private void addSpectrum_1() {
        Spectrum spectrum = new Spectrum();

        spectrum.setId(SPECTRUM_1_ID);
        spectrum.setProjectAccession(PROJECT_1_ACCESSION);
        spectrum.setAssayAccession(ASSAY_1_1_ACCESSION);

        SpectrumIndexService spectrumIndexService = new SpectrumIndexService(this.solrSpectrumRepositoryFactory.create(), server);
        spectrumIndexService.save(spectrum);
    }

    private void addSpectrum_2() {
        Spectrum spectrum = new Spectrum();

        spectrum.setId(SPECTRUM_2_ID);
        spectrum.setProjectAccession(PROJECT_2_ACCESSION);
        spectrum.setAssayAccession(ASSAY_2_1_ACCESSION);

        SpectrumIndexService spectrumIndexService = new SpectrumIndexService(this.solrSpectrumRepositoryFactory.create(), server);
        spectrumIndexService.save(spectrum);
    }

    private void addSpectrum_3() {
        Spectrum spectrum = new Spectrum();

        spectrum.setId(SPECTRUM_3_ID);
        spectrum.setProjectAccession(PROJECT_2_ACCESSION);
        spectrum.setAssayAccession(ASSAY_2_2_ACCESSION);

        SpectrumIndexService spectrumIndexService = new SpectrumIndexService(this.solrSpectrumRepositoryFactory.create(), server);
        spectrumIndexService.save(spectrum);

    }

}
