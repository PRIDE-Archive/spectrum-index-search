package uk.ac.ebi.pride.spectrumindex.search.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.util.SpectrumJmzReaderMapper;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;

import java.io.File;
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

    public void indexAllSpectraForProjectAndAssay(String projectAccession, String assayAccession, File mgfFile){
        LinkedList<Spectrum> spectra = new LinkedList<Spectrum>();

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();

        try {
            MgfFile inputParser = new MgfFile(mgfFile);
            List<Spectrum> spectraToIndex = new LinkedList<Spectrum>();
            for (int i=0; i<inputParser.getSpectraCount();i++) {
                uk.ac.ebi.pride.tools.jmzreader.model.Spectrum spectrum = inputParser.getSpectrumByIndex(i);
                Spectrum solrSpectrum = SpectrumJmzReaderMapper.createSolrSpectrum(projectAccession, assayAccession, spectrum);
                spectraToIndex.add(solrSpectrum);
            }
            spectrumIndexService.save(spectraToIndex);

        } catch (JMzReaderException e) {
            e.printStackTrace();
        }


        endTime = System.currentTimeMillis();
        logger.info("DONE indexing all PSMs for project " + projectAccession + " in " + (double) (endTime - startTime) / 1000.0 + " seconds");

    }

    public void deleteAllPsmsForProject(String projectAccession) {

        // search by project accession
        List<Spectrum> spectra = this.spectrumSearchService.findByProjectAccession(projectAccession);
        this.spectrumIndexService.delete(spectra);

    }

}
