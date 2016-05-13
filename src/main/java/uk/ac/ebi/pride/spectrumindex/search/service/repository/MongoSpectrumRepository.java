package uk.ac.ebi.pride.spectrumindex.search.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;

import java.util.Collection;
import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 *
 * Note: using the Query annotation allows wildcards to go straight into the query
 */
@Repository
public interface MongoSpectrumRepository extends MongoRepository<Spectrum, String> {

    // Project accession methods
    Page<Spectrum> findByProjectAccession(String projectAccession, Pageable pageable);

    //For deleting documents, it is not exposed to the users
    List<Spectrum> findByProjectAccession(String projectAccession);

    Long countByProjectAccession(String projectAccession);

    Page<Spectrum> findByProjectAccessionIn(Collection<String> projectAccessions, Pageable pageable);

    // Assay accession methods
    Page<Spectrum> findByAssayAccession(String assayAccession, Pageable pageable);

    Long countByAssayAccession(String assayAccession);

    Page<Spectrum> findByAssayAccessionIn(Collection<String> assayAccessions, Pageable pageable);

//    void deleteByProjectAccession(String projectAccession);  It is not available in this version of spring-data-monogodb; implemented in the index-service
}
