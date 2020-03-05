package edu.asu.diging.citesphere.config.core;

import java.net.URISyntaxException;
import java.util.Properties;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import edu.asu.diging.citesphere.core.email.impl.NotSetupMailSender;

@Configuration
@EnableCaching
@EnableAsync
@PropertySource("classpath:/config.properties")
public class CitesphereConfig {
    
    @Value("${email.username}")
    private String emailUser;

    @Value("${email.password}")
    private String emailPassword;

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private String emailPort;
    
    @Value("${email.transport.protocol}")
    private String emailTransportProtocol;
    
    @Value("${email.smtp.auth}")
    private String emailSmtpAuth;
    
    @Value("${email.smtp.starttls.enable}")
    private String emailStartTls;

    @Value("${email.debug}")
    private String emailDebug;
    
    @Value("${max_upload_size}")
    private String maxUploadSize;

    
    @Bean
    public CacheManager cacheManager() throws URISyntaxException {
        CacheManagerBuilder.newCacheManagerBuilder().withCache("preConfigured", CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10))).build();
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager manager = cachingProvider
                .getCacheManager(getClass().getResource("/ehcache.xml").toURI(), getClass().getClassLoader());
        JCacheCacheManager cacheManager = new JCacheCacheManager(manager);
        return cacheManager;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        if (emailHost == null || emailHost.isEmpty()) {
            return new NotSetupMailSender();
        }
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(emailHost);
        sender.setPort(new Integer(emailPort));
        sender.setPassword(emailPassword);
        sender.setUsername(emailUser);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.transport.protocol", emailTransportProtocol);
        javaMailProperties.put("mail.smtp.auth", emailSmtpAuth);
        javaMailProperties.put("mail.smtp.starttls.enable", emailStartTls);
        javaMailProperties.put("mail.debug", emailDebug);
        sender.setJavaMailProperties(javaMailProperties);

        return sender;
    }
    
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(new Long(maxUploadSize));
        return multipartResolver;
    }
}
