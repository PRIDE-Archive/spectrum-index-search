package uk.ac.ebi.pride.spectrumindex.search.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;

import java.util.Collection;

@Repository
public interface MongoSpectrumRepository extends MongoRepository<Spectrum, String> {

  // Project accession methods
  Page<Spectrum> findByProjectAccession(String projectAccession, Pageable pageable);

  Long countByProjectAccession(String projectAccession);

  Page<Spectrum> findByProjectAccessionIn(Collection<String> projectAccessions, Pageable pageable);

  // Assay accession methods
  Page<Spectrum> findByAssayAccession(String assayAccession, Pageable pageable);

  Long countByAssayAccession(String assayAccession);

  Page<Spectrum> findByAssayAccessionIn(Collection<String> assayAccessions, Pageable pageable);
}
