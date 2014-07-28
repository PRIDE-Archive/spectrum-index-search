package uk.ac.ebi.pride.spectrumindex.search.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;

import java.util.Collection;
import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 * Note: using the Query annotation allows wildcards to go straight into the query
 */
public interface SolrSpectrumRepository extends SolrCrudRepository<Spectrum, String> {

    // ToDo: add count methods

    // ID query methods
    @Query("id:?0")
    List<Spectrum> findById(String id);
    @Query("id:?0")
    List<Spectrum> findByIdIn(Collection<String> id);


    // Project accession methods
    @Query("project_accession:?0")
    Page<Spectrum> findByProjectAccession(String projectAccession, Pageable pageable);
    @Query("project_accession:(?0)")
    Page<Spectrum> findByProjectAccessionIn(Collection<String> projectAccessions, Pageable pageable);

    // Assay accession methods
    @Query("assay_accession:?0")
    Page<Spectrum> findByAssayAccession(String assayAccession, Pageable pageable);
    @Query("assay_accession:(?0)")
    Page<Spectrum> findByAssayAccessionIn(Collection<String> assayAccessions, Pageable pageable);

    void deleteByProjectAccession(String projectAccession);
}
