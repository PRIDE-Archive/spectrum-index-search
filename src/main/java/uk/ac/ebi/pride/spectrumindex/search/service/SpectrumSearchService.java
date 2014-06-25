package uk.ac.ebi.pride.spectrumindex.search.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.SolrSpectrumRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 */
@Service
public class SpectrumSearchService {

    private SolrSpectrumRepository solrSpectrumRepository;

    public SpectrumSearchService(SolrSpectrumRepository solrSpectrumRepository) {
        this.solrSpectrumRepository = solrSpectrumRepository;
    }

    public void setSolrSpectrumRepository(SolrSpectrumRepository solrSpectrumRepository) {
        this.solrSpectrumRepository = solrSpectrumRepository;
    }

    // find by ID methods
    public List<Spectrum> findById(String id) {
        return solrSpectrumRepository.findById(id);
    }

    public List<Spectrum> findById(Collection<String> ids) {
        return solrSpectrumRepository.findByIdIn(ids);
    }


    // find by project accession methods
    public List<Spectrum> findByProjectAccession(String projectAccession) {
        return solrSpectrumRepository.findByProjectAccession(projectAccession);
    }

    public List<Spectrum> findByProjectAccession(Collection<String> projectAccessions) {
        return solrSpectrumRepository.findByIdIn(projectAccessions);
    }


}
