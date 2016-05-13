package uk.ac.ebi.pride.spectrumindex.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.MongoSpectrumRepository;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 */
@Service
public class SpectrumIndexService {

    private static Logger logger = LoggerFactory.getLogger(SpectrumIndexService.class.getName());

    @Resource
    private MongoSpectrumRepository mongoSpectrumRepository;

    public SpectrumIndexService() {
    }

    public void setMongoSpectrumRepository(MongoSpectrumRepository mongoSpectrumRepository) {
        this.mongoSpectrumRepository = mongoSpectrumRepository;
    }

    @Transactional
    public void save(Spectrum spectrum) {
        mongoSpectrumRepository.save(spectrum);
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
            mongoSpectrumRepository.save(spectra);
        }
    }

    @Transactional
    public void delete(Spectrum spectrum){
        mongoSpectrumRepository.delete(spectrum);
    }

    @Transactional
    public void delete(Iterable<Spectrum> spectra){
        if (spectra==null || !spectra.iterator().hasNext())
            logger.debug("No Spectra to delete");
        else {
            mongoSpectrumRepository.delete(spectra);
        }
    }

    @Transactional
    public void deleteAll() {
        mongoSpectrumRepository.deleteAll();
    }

    @Transactional
    public void deleteByProjectAccession(String projectAccession) {
        //Possible improvement, retrieve the ids to be deleted instead of the objects
        mongoSpectrumRepository.delete(mongoSpectrumRepository.findByProjectAccession(projectAccession));
    }

    @Transactional
    public Iterable<Spectrum> save(Collection<Spectrum> spectra) {
            return mongoSpectrumRepository.save(spectra);
    }

}
