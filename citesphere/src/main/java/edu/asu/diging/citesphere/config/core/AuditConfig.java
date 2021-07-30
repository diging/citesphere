package edu.asu.diging.citesphere.config.core;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.mongo.MongoRepository;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.mongodb.MongoClient;

@Configuration
public class AuditConfig {

    @Autowired
    private PlatformTransactionManager txManager;

    @Bean
    public Javers javers() {
        MongoRepository javersMongoRepository =
                new MongoRepository(mongo().getDatabase("citesphere"));

        return JaversBuilder.javers()
                .registerJaversRepository(javersMongoRepository)
                .build();
    }
    
    public MongoClient mongo() {
        return new MongoClient();
    }

    @Bean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
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