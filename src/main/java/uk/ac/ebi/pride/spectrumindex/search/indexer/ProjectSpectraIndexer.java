package uk.ac.ebi.pride.spectrumindex.search.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;

import java.util.*;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class ProjectSpectraIndexer {

    private static Logger logger = LoggerFactory.getLogger(ProjectSpectraIndexer.class.getName());

    private SpectrumSearchService spectrumSearchService;
    private SpectrumIndexService spectrumIndexService;

    public ProjectSpectraIndexer(SpectrumSearchService spectrumSearchService, SpectrumIndexService spectrumIndexService) {
        this.spectrumSearchService = spectrumSearchService;
        this.spectrumIndexService = spectrumIndexService;
    }

    public void indexAllSpectraForProjectAndAssay(String projectAccession, String assayAccession){
        LinkedList<Spectrum> spectra = new LinkedList<Spectrum>();

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();



        endTime = System.currentTimeMillis();
        logger.info("DONE indexing all PSMs for project " + projectAccession + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

    }

    public void deleteAllPsmsForProject(String projectAccession) {

        // search by project accession
        List<Spectrum> spectra = this.spectrumSearchService.findByProjectAccession(projectAccession);
        this.spectrumIndexService.delete(spectra);

    }

}
