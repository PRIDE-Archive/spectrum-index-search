package uk.ac.ebi.pride.spectrumindex.search.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.MongoSpectrumRepository;

import javax.annotation.Resource;
import java.util.Collection;

/** Service to search spectra. */
@Service
public class SpectrumSearchService {

  @Resource private MongoSpectrumRepository mongoSpectrumRepository;

  /** Default constructor. */
  public SpectrumSearchService() {}

  /**
   * Sets the spectra repository.
   *
   * @param mongoSpectrumRepository the spectra repository
   */
  public void setMongoSpectrumRepository(MongoSpectrumRepository mongoSpectrumRepository) {
    this.mongoSpectrumRepository = mongoSpectrumRepository;
  }

  /**
   * Finds spectra by ID.
   *
   * @param id the ID to search for
   * @return the spectrum
   */
  public Spectrum findById(String id) {
    return mongoSpectrumRepository.findById(id).orElse(new Spectrum());
  }

  // find by project accession methods

  /**
   * Finds spectra by project accession and page.
   *
   * @param projectAccession the project accession
   * @param pageable the page
   * @return a page of spectra
   */
  public Page<Spectrum> findByProjectAccession(String projectAccession, Pageable pageable) {
    return mongoSpectrumRepository.findByProjectAccession(projectAccession, pageable);
  }

  /**
   * Counts spectra by project accession.
   *
   * @param projectAccession the project accession
   * @return the count of spectra for the project accession
   */
  @SuppressWarnings("WeakerAccess")
  public Long countByProjectAccession(String projectAccession) {
    return mongoSpectrumRepository.countByProjectAccession(projectAccession);
  }

  /**
   * Finds spectra by a collection of project accessions and page.
   *
   * @param projectAccessions a collection of project accessions
   * @param pageable the page to search on
   * @return a page pf spectra
   */
  @SuppressWarnings("WeakerAccess")
  public Page<Spectrum> findByProjectAccession(
      Collection<String> projectAccessions, Pageable pageable) {
    return mongoSpectrumRepository.findByProjectAccessionIn(projectAccessions, pageable);
  }

  // Assay accession methods
  /**
   * Finds spectra by assay accession and page.
   *
   * @param assayAccession the assay accession
   * @param pageable the page to search on
   * @return a page of spectra
   */
  public Page<Spectrum> findByAssayAccession(String assayAccession, Pageable pageable) {
    return mongoSpectrumRepository.findByAssayAccession(assayAccession, pageable);
  }

  /**
   * Counts spectra by assay accession.
   *
   * @param assayAccession the assay accession
   * @return the count of spectra for the project accession
   */
  @SuppressWarnings("WeakerAccess")
  public Long countByAssayAccession(String assayAccession) {
    return mongoSpectrumRepository.countByAssayAccession(assayAccession);
  }

  /**
   * Finds spectra by a collection of assay accessions and page.
   *
   * @param assayAccessions a collection of assay accessions
   * @param pageable the page to search on
   * @return a page pf spectra
   */
  @SuppressWarnings("WeakerAccess")
  public Page<Spectrum> findByAssayAccession(
      Collection<String> assayAccessions, Pageable pageable) {
    return mongoSpectrumRepository.findByAssayAccessionIn(assayAccessions, pageable);
  }
}
