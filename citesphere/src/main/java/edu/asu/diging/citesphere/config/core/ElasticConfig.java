package edu.asu.diging.citesphere.config.core;

import java.io.IOException;
import java.net.UnknownHostException;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.TerminalClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.annotation.Backoff;


@Configuration
@EnableRetry
@EnableElasticsearchRepositories(basePackages = { "edu.asu.diging.citesphere.core.search.data" })
@PropertySource({ "classpath:config.properties", "${appConfigFile:classpath:}/app.properties" })
public class ElasticConfig extends ElasticsearchConfigurationSupport {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${elasticsearch.indexName}")
    private String indexName;
    
    @Value("${elasticsearch.host}")
    private String host;
    
    @Value("${elasticsearch.user}")
    private String user;

    @Value("${elasticsearch.password}")
    private String password;
    
    @Value("${elasticsearch.path.prefix}")
    private String pathPrefix;
    
    @Value("${elasticsearch.connect.timeout}")
    private long connectTimeout;

    @Bean
    public String indexName() {
        return indexName;
    }
    
    @Bean
    public RestHighLevelClient elasticsearchRestClient() {
        logger.info("Connecting to ES at: " + host);
        TerminalClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(host).withConnectTimeout(connectTimeout).withSocketTimeout(connectTimeout);
                //.usingSsl();
        if (user != null && !user.trim().isEmpty()) {
            logger.info("Using user info: " + user);
            builder.withBasicAuth(user, password); 
        }
        if (pathPrefix != null && !pathPrefix.trim().isEmpty()) {
            logger.info("Using path prefix: " + pathPrefix);
            builder.withPathPrefix(pathPrefix);
        }
        final ClientConfiguration clientConfiguration = builder.build();
        return RestClients.create(clientConfiguration).rest();
    }


    @Bean(name = { "elasticsearchOperations", "elasticsearchTemplate" })
    public ElasticsearchRestTemplate elasticsearchTemplate() throws UnknownHostException {
        return new ElasticsearchRestTemplate(elasticsearchRestClient());
    }
    
    @Bean
    public RetryTemplate retryTemplate() {
        return new RetryTemplate();
    }
    
    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 1000))
    public void connectToElasticsearch() {
        try {
            RequestOptions options = RequestOptions.DEFAULT;
            elasticsearchRestClient().ping(options);
//            elasticsearchRestClient().info();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Elasticsearch is not running: " + e.getMessage());
            e.printStackTrace();
        }
    }

}