package edu.asu.diging.citesphere.config.core;

import java.util.Collections;
import java.util.Map;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mongodb.client.MongoClient;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.impl.Citation;

@Configuration
@EnableAspectJAutoProxy
public class AuditConfig {

    @Autowired
    private MongoClient mongoClient;

    @Value("${mongo.database.name}")
    private String mongoDbName;
    
    @Value("${javers_default_author}")
    private String javersDefaultAuthor;

    private static final String CITATION_KEY = "key";
    private static final String GROUP_PROPERTY = "group";

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
        return new AuthorProvider() {
            @Override
            public String provide() {
                Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
                if (auth == null) {
                    return javersDefaultAuthor;
                }
                return auth.getName();
            }
        };
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provideForCommittedObject(Object domainObject) {
                if (domainObject instanceof ICitation) {
                    return Collections.singletonMap(GROUP_PROPERTY, ((ICitation) domainObject).getGroup());
                }
                return Collections.emptyMap();
            }
        };
    }
}