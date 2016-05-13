package uk.ac.ebi.pride.spectrumindex.search.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
public class SpectrumSearchService {

    @Resource
    private MongoSpectrumRepository mongoSpectrumRepository;

    public SpectrumSearchService() {
    }

    public void setMongoSpectrumRepository(MongoSpectrumRepository mongoSpectrumRepository) {
        this.mongoSpectrumRepository = mongoSpectrumRepository;
    }

    // find by ID methods
    public Spectrum findById(String id) {
        return mongoSpectrumRepository.findOne(id);
    }

    // find by project accession methods
    public Page<Spectrum> findByProjectAccession(String projectAccession, Pageable pageable) {
        return mongoSpectrumRepository.findByProjectAccession(projectAccession, pageable);
    }

    public Long countByProjectAccession(String projectAccession) {
        return  mongoSpectrumRepository.countByProjectAccession(projectAccession);
    }

    public Page<Spectrum> findByProjectAccession(Collection<String> projectAccessions, Pageable pageable) {
        return mongoSpectrumRepository.findByProjectAccessionIn(projectAccessions, pageable);
    }

    // find by assay accession methods
    public Page<Spectrum> findByAssayAccession(String assayAccession, Pageable pageable) {
        return mongoSpectrumRepository.findByAssayAccession(assayAccession, pageable);
    }

    public Long countByAssayAccession(String assayAccession) {
        return mongoSpectrumRepository.countByAssayAccession(assayAccession);
    }

    public Page<Spectrum> findByAssayAccession(Collection<String> assayAccessions, Pageable pageable) {
        return mongoSpectrumRepository.findByAssayAccessionIn(assayAccessions, pageable);
    }
}
