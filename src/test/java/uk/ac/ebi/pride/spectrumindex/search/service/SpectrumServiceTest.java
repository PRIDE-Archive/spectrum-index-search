package uk.ac.ebi.pride.spectrumindex.search.service;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.model.SpectrumFields;
import uk.ac.ebi.pride.spectrumindex.search.service.repository.SolrSpectrumRepositoryFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SpectrumServiceTest extends SolrTestCaseJ4 {




}
