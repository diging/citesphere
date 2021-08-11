package edu.asu.diging.citesphere.config.core;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.mongodb.client.MongoClient;

import edu.asu.diging.citesphere.model.bib.impl.Citation;

@Configuration
@EnableAspectJAutoProxy
public class AuditConfig {

    @Autowired
    private MongoClient mongoClient;
    
    @Value("${mongo.database.name}")
    private String mongoDbName;
    
    private static final String CITATION_KEY = "key";

    @Bean
    public Javers javers() {
        MongoRepository javersMongoRepository = new MongoRepository(mongoClient.getDatabase(mongoDbName));

        return JaversBuilder.javers().registerEntity(new EntityDefinition(Citation.class, CITATION_KEY))
                .registerJaversRepository(javersMongoRepository).build();
    }

    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        return new JaversSpringDataAuditableRepositoryAspect(javers(), authorProvider(), commitPropertiesProvider());
    }

    @Bean
    public AuthorProvider authorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new EmptyPropertiesProvider();
    }
}