package uk.ac.ebi.pride.spectrumindex.search.config;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/** Configures Fongo for unit tests. */
@ComponentScan(basePackages = "uk.ac.ebi.pride.spectrumindex.search.service")
@EnableMongoRepositories(basePackages = "uk.ac.ebi.pride.spectrumindex.search.service.repository")
@Configuration
public class MongoTestConfiguration extends AbstractMongoConfiguration {

  @SuppressWarnings("NullableProblems")
  @Override
  protected String getDatabaseName() {
    return "mongo-unit-test";
  }

  @SuppressWarnings("NullableProblems")
  @Bean
  @Override
  public MongoClient mongoClient() {
    return new Fongo("mongo-test").getMongo();
  }
}
