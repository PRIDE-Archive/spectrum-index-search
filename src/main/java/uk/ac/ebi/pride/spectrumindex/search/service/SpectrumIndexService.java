package uk.ac.ebi.pride.spectrumindex.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.SolrSpectrumRepository;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 */
@Service
public class SpectrumIndexService {

    private static Logger logger = LoggerFactory.getLogger(SpectrumIndexService.class.getName());

    private SolrSpectrumRepository solrSpectrumRepository;

    public SpectrumIndexService(SolrSpectrumRepository solrSpectrumRepository) {
        this.solrSpectrumRepository = solrSpectrumRepository;
    }

    public void setSolrSpectrumRepository(SolrSpectrumRepository solrSpectrumRepository) {
        this.solrSpectrumRepository = solrSpectrumRepository;
    }

    public void save(Spectrum spectrum) {
        // fix the accession of needed
        //TODO
//        logger.info("Saving PSM with accession " + psm.getId());
//        psm.setId(PsmIdCleaner.cleanId(psm.getId()));
        solrSpectrumRepository.save(spectrum);
    }

    public void save(Iterable<Spectrum> spectra) {
        if (spectra==null || !spectra.iterator().hasNext())
            logger.debug("No Spectrum to save");
        else {


            for (Spectrum spectrum: spectra) {
                logger.debug("Saving Spectra: " + spectrum.getId());
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
}
