package edu.asu.diging.citesphere.config.core;

import java.net.UnknownHostException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
@EnableMongoRepositories({ "edu.asu.diging.citesphere.core.mongo", "edu.asu.diging.citesphere.data.bib.impl" })
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
        return new MongoTemplate(mongoDbFactory());
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
}