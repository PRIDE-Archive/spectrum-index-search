package uk.ac.ebi.pride.spectrumindex.search.service;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.UncategorizedSolrException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.SolrSpectrumRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 */
@Service
public class SpectrumIndexService {

    private static Logger logger = LoggerFactory.getLogger(SpectrumIndexService.class.getName());

    private static final int NUM_TRIES = 10;
    private static final int SECONDS_TO_WAIT = 30;
    private static final long MAX_ELAPSED_TIME_PING_QUERY = 10000;

    private SolrSpectrumRepository solrSpectrumRepository;
    private SolrServer spectrumSolrServer;

    public SpectrumIndexService(SolrSpectrumRepository solrSpectrumRepository, SolrServer spectrumSolrServer) {
        this.solrSpectrumRepository = solrSpectrumRepository;
        this.spectrumSolrServer = spectrumSolrServer;
    }

    public void setSolrSpectrumRepository(SolrSpectrumRepository solrSpectrumRepository) {
        this.solrSpectrumRepository = solrSpectrumRepository;
    }

    public void save(Spectrum spectrum) {
        // fix the accession of needed
//        spectrum.setId(SpectrumIdCleaner.getCleanSpectrumId(spectrum.getId()));
        solrSpectrumRepository.save(spectrum);
    }

    public void save(Iterable<Spectrum> spectra) {
        if (spectra==null || !spectra.iterator().hasNext())
            logger.debug("No Spectrum to save");
        else {
            int i = 0;
            for (Spectrum spectrum: spectra) {
//                spectrum.setId(SpectrumIdCleaner.getCleanSpectrumId(spectrum.getId()));
                logger.debug("Saving Spectra " + i + " with ID: " + spectrum.getId());
                logger.debug("Project: " + spectrum.getProjectAccession());
                logger.debug("Assay: " + spectrum.getAssayAccession());
                logger.debug("Num PeaksI: " + spectrum.getPeaksIntensities().length);
                logger.debug("Num PeaksM: " + spectrum.getPeaksMz().length);
                i++;
            }
            solrSpectrumRepository.save(spectra);
        }
    }

    public void delete(Spectrum spectrum){
        solrSpectrumRepository.delete(spectrum);
    }

    public void delete(Iterable<Spectrum> psms){
        if (psms==null || !psms.iterator().hasNext())
            logger.debug("No Spectra to delete");
        else {
            solrSpectrumRepository.delete(psms);
        }
    }

    public void deleteAll() {
        solrSpectrumRepository.deleteAll();
    }

    public void deleteByProjectId(String projectAccession) {
        solrSpectrumRepository.deleteByProjectAccession(projectAccession);
    }

    public boolean reliableSave(Collection<Spectrum> spectraToIndex) {
        int numTries = 0;
        boolean succeed = false;
        while (numTries<NUM_TRIES && !succeed) {
            try {
                SolrPingResponse pingResponse = this.spectrumSolrServer.ping();
                if ((pingResponse.getStatus() == 0) && pingResponse.getElapsedTime() < MAX_ELAPSED_TIME_PING_QUERY) {
                    this.spectrumSolrServer.addBeans(spectraToIndex);
                    this.spectrumSolrServer.commit();
                    succeed = true;
                } else {
                    logger.info("[TRY " + numTries + " Solr server too busy!");
                    logger.info("PING response status: " + pingResponse.getStatus());
                    logger.info("PING elapsed time: " + pingResponse.getElapsedTime());
                    logger.info("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                    waitSecs();
                }
            } catch (SolrServerException e) {
                logger.error("[TRY " + numTries + "] There are server problems: " + e.getCause());
                logger.error("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                waitSecs();
            } catch (UncategorizedSolrException e) {
                logger.error("[TRY " + numTries + "] There are server problems: " + e.getCause());
                logger.error("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                waitSecs();
            } catch (Exception e) {
                logger.error("[TRY " + numTries + "] There are UNKNOWN problems: " + e.getCause());
                logger.error("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                waitSecs();
            }
            numTries++;
        }

        return succeed;
    }

    private void waitSecs() {
        try {
            Thread.sleep(SECONDS_TO_WAIT * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
