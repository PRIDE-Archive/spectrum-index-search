package uk.ac.ebi.pride.spectrumindex.search.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;

import java.util.Collection;

/** The MongoDB repistory to save, search, and delete spectra data. */
@Repository
public interface MongoSpectrumRepository extends MongoRepository<Spectrum, String> {

  // Project accession methods
  /**
   * Finds spectra by project accession and page.
   *
   * @param projectAccession the project accession
   * @param pageable the page to search on
   * @return a page of spectra
   */
  Page<Spectrum> findByProjectAccession(String projectAccession, Pageable pageable);

  /**
   * Counts spectra by project accession.
   *
   * @param projectAccession the project accession
   * @return the count of spectra for the project accession
   */
  Long countByProjectAccession(String projectAccession);

  /**
   * Finds spectra by a collection of project accessions and page.
   *
   * @param projectAccessions a collection of project accessions
   * @param pageable the page to search on
   * @return a page pf spectra
   */
  Page<Spectrum> findByProjectAccessionIn(Collection<String> projectAccessions, Pageable pageable);

  // Assay accession methods
  /**
   * Finds spectra by assay accession and page.
   *
   * @param assayAccession the assay accession
   * @param pageable the page to search on
   * @return a page of spectra
   */
  Page<Spectrum> findByAssayAccession(String assayAccession, Pageable pageable);

  /**
   * Counts spectra by assay accession.
   *
   * @param assayAccession the assay accession
   * @return the count of spectra for the project accession
   */
  Long countByAssayAccession(String assayAccession);

  /**
   * Finds spectra by a collection of assay accessions and page.
   *
   * @param assayAccessions a collection of assay accessions
   * @param pageable the page to search on
   * @return a page pf spectra
   */
  Page<Spectrum> findByAssayAccessionIn(Collection<String> assayAccessions, Pageable pageable);
}
