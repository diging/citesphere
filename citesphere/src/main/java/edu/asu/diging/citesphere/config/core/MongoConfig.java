package edu.asu.diging.citesphere.config.core;

import java.net.UnknownHostException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import edu.asu.diging.citesphere.user.IUser;

@Configuration
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
@EnableMongoRepositories({ "edu.asu.diging.citesphere.core.mongo", "edu.asu.diging.citesphere.data.bib" })
public class MongoConfig {

    @Value("${mongo.database.name}")
    private String mongoDbName;

    @Value("${mongo.database.host}")
    private String mongoDbHost;

    @Value("${mongo.database.port}")
    private int mongoDbPort;
    
    @Value("${mongo.database.user}")
    private String mongoDbUser;
    
    @Value("${mongo.database.password}")
    private String mongoDbPassword;
    
    @Value("${mongo.database.authdb}")
    private String mongoDbAuthdb;

    @Bean
    public MongoClient mongo() throws UnknownHostException {
        Builder builder = MongoClientSettings.builder();
        if (mongoDbUser != null && !mongoDbUser.trim().isEmpty()) {
            builder = builder.credential(MongoCredential.createCredential(mongoDbUser, mongoDbAuthdb, mongoDbPassword.toCharArray()));
        }
        MongoClient mongoClient = MongoClients
                .create(builder
                        .applyToClusterSettings(
                                b -> b.hosts(Arrays.asList(new ServerAddress(mongoDbHost, mongoDbPort))))
                        .build());
        return mongoClient;
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory() throws UnknownHostException {
        return new SimpleMongoClientDatabaseFactory(mongo(), mongoDbName);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setAutoIndexCreation(true);      
        
        MongoDatabaseFactory dbFactory = mongoDbFactory();
        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(dbFactory);
        
        return new MongoTemplate(dbFactory, new MappingMongoConverter(dbRefResolver, mappingContext));
    }
    
    @WritingConverter
    @Component
    class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, String> {
        @Override
        public String convert(OffsetDateTime source) {
            return source.toInstant().atZone(ZoneOffset.UTC).toString();
        }
    }

    @ReadingConverter
    @Component
    class OffsetDateTimeReadConverter implements Converter<String, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(String source) {
            return OffsetDateTime.parse(source);
        }
    }
    
//    @Configuration
//    public class MongoIndex implements InitializingBean {
//
//        @Autowired
//        private MongoTemplate mongoTemplate;
//
//        @PostConstruct
//        public void afterPropertiesSet() throws Exception {
//            IndexOperations indexOps = mongoTemplate.indexOps(IUser.class);
//            indexOps.ensureIndex(new Index().on("email", org.springframework.data.domain.Sort.Direction.ASC).unique());
//        }
//    }
}