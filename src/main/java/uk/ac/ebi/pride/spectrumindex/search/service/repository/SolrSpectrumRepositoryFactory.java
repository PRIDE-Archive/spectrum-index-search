package uk.ac.ebi.pride.spectrumindex.search.service.repository;

import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
public class SolrSpectrumRepositoryFactory {

    private SolrOperations solrOperations;

    public SolrSpectrumRepositoryFactory(SolrOperations solrOperations) {
        this.solrOperations = solrOperations;
    }

    public SolrSpectrumRepository create() {
        return new SolrRepositoryFactory(this.solrOperations).getRepository(SolrSpectrumRepository.class);
    }

}