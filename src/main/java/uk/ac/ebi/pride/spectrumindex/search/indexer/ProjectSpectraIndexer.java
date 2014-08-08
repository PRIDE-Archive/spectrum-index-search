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

    private static Logger logger = LoggerFactory.getLogger(ProjectSpectraIndexer.class.getName());

    private static final int INDEXING_SIZE_STEP = 100;

    private SpectrumSearchService spectrumSearchService;
    private SpectrumIndexService spectrumIndexService;


    public ProjectSpectraIndexer(SpectrumSearchService spectrumSearchService, SpectrumIndexService spectrumIndexService) {
        this.spectrumSearchService = spectrumSearchService;
        this.spectrumIndexService = spectrumIndexService;
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
                    if (this.spectrumIndexService.reliableSave(spectraToIndex)) {
                        spectraToIndex = new LinkedList<Spectrum>();
                    }
                }
            }
            this.spectrumIndexService.reliableSave(spectraToIndex);

        } catch (JMzReaderException e) {
            e.printStackTrace();
        }


        endTime = System.currentTimeMillis();
        logger.info("DONE indexing all PSMs for assay " + assayAccession + " in project " + projectAccession + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

    }


    public void deleteAllPsmsForProject(String projectAccession) {

        this.spectrumIndexService.deleteByProjectId(projectAccession);

    }

}
