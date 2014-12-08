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

import java.util.Collection;

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

    public SpectrumIndexService() {
    }

    public SpectrumIndexService(SolrSpectrumRepository solrSpectrumRepository, SolrServer spectrumSolrServer) {
        this.solrSpectrumRepository = solrSpectrumRepository;
        this.spectrumSolrServer = spectrumSolrServer;
    }

    public void setSolrSpectrumRepository(SolrSpectrumRepository solrSpectrumRepository) {
        this.solrSpectrumRepository = solrSpectrumRepository;
    }

    @Transactional
    public void save(Spectrum spectrum) {
        solrSpectrumRepository.save(spectrum);
    }

    @Transactional
    public void save(Iterable<Spectrum> spectra) {
        if (spectra==null || !spectra.iterator().hasNext())
            logger.debug("No Spectrum to save");
        else {
            int i = 0;
            for (Spectrum spectrum: spectra) {
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

    @Transactional
    public void delete(Spectrum spectrum){
        solrSpectrumRepository.delete(spectrum);
    }

    @Transactional
    public void delete(Iterable<Spectrum> spectra){
        if (spectra==null || !spectra.iterator().hasNext())
            logger.debug("No Spectra to delete");
        else {
            solrSpectrumRepository.delete(spectra);
        }
    }

    @Transactional
    public void deleteAll() {
        solrSpectrumRepository.deleteAll();
    }

    @Transactional
    public void deleteByProjectId(String projectAccession) {
        solrSpectrumRepository.deleteByProjectAccession(projectAccession);
    }

    @Transactional
    public boolean reliableSave(Collection<Spectrum> spectra) {
        if (spectra!= null && spectra.size()>0) {
            int numTries = 0;
            boolean succeed = false;
            while (numTries < NUM_TRIES && !succeed) {
                try {
                    SolrPingResponse pingResponse = this.spectrumSolrServer.ping();
                    if ((pingResponse.getStatus() == 0) && pingResponse.getElapsedTime() < MAX_ELAPSED_TIME_PING_QUERY) {
                        //We leave solr to handle internally the commit in a maximum of 4 min
                        this.spectrumSolrServer.addBeans(spectra, 480000);
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
                    e.printStackTrace();
                    logger.error("Re-trying in " + SECONDS_TO_WAIT + " seconds...");
                    waitSecs();
                }
                numTries++;
            }

            return succeed;
        } else {
            logger.error("SpectrumIndexService [reliable-save]: Trying to save empty spectra!");

            return false;
        }
    }

    private void waitSecs() {
        try {
            Thread.sleep(SECONDS_TO_WAIT * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
