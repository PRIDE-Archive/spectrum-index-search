package uk.ac.ebi.pride.spectrumindex.search.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.UncategorizedSolrException;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.util.SpectrumJmzReaderMapper;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.model.Ms2Query;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class ProjectSpectraIndexer {

    private static final int NUM_TRIES = 10;
    private static final int SECONDS_TO_WAIT = 30;
    private static final long MAX_ELAPSED_TIME_PING_QUERY = 10000;
    private static Logger logger = LoggerFactory.getLogger(ProjectSpectraIndexer.class.getName());

    private static final int INDEXING_SIZE_STEP = 100;

    private SpectrumSearchService spectrumSearchService;
    private SpectrumIndexService spectrumIndexService;
    private SolrServer spectrumSolrServer;


    public ProjectSpectraIndexer(SpectrumSearchService spectrumSearchService, SpectrumIndexService spectrumIndexService, SolrServer spectrumSolrServer) {
        this.spectrumSearchService = spectrumSearchService;
        this.spectrumIndexService = spectrumIndexService;
        this.spectrumSolrServer = spectrumSolrServer;
    }

    public void indexAllSpectraForProjectAndAssay(String projectAccession, String assayAccession, File mgfFile){

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();

        try {
            MgfFile inputParser = new MgfFile(mgfFile);
            List<Spectrum> spectraToIndex = new LinkedList<Spectrum>();
            logger.info("There are " + inputParser.getSpectraCount() + " spectra to index");
            for (int i=1; i<=inputParser.getSpectraCount();i++) {
                Ms2Query spectrum = (Ms2Query) inputParser.getSpectrumByIndex(i);
                Spectrum solrSpectrum = SpectrumJmzReaderMapper.createSolrSpectrum(projectAccession, assayAccession, spectrum);
                spectraToIndex.add(solrSpectrum);
                if (spectraToIndex.size() >= INDEXING_SIZE_STEP) {
                    int numTries = 0;
                    boolean succeed = false;
                    while (numTries<NUM_TRIES && !succeed) {
                        try {
                            SolrPingResponse pingResponse = this.spectrumSolrServer.ping();
                            if ((pingResponse.getStatus() == 0) && pingResponse.getElapsedTime() < MAX_ELAPSED_TIME_PING_QUERY) {
                                spectrumIndexService.save(spectraToIndex);
                                spectraToIndex = new LinkedList<Spectrum>();
                                succeed = true;
                            } else {
                                logger.info("Solr server too busy!");
                                logger.info("PING response status: " + pingResponse.getStatus());
                                logger.info("PING elapsed time: " + pingResponse.getElapsedTime());
                            }
                        } catch (UncategorizedSolrException e) {
                            logger.info("[TRY " + numTries + "] There are server problems: " + e.getCause());
                            logger.info("Re-trying in "+ SECONDS_TO_WAIT + " seconds...");
                            waitSecs();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SolrServerException e) {
                            e.printStackTrace();
                        }
                        numTries++;
                    }
                }
            }
            spectrumIndexService.save(spectraToIndex);

        } catch (JMzReaderException e) {
            e.printStackTrace();
        }


        endTime = System.currentTimeMillis();
        logger.info("DONE indexing all PSMs for project " + projectAccession + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

    }

    private void waitSecs() {
        try {
            Thread.sleep(SECONDS_TO_WAIT * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public void deleteAllPsmsForProject(String projectAccession) {

        this.spectrumIndexService.deleteByProjectId(projectAccession);

    }

}
