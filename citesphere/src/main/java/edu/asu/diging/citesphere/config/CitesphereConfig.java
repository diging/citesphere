package edu.asu.diging.citesphere.config;

import java.net.URISyntaxException;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CitesphereConfig {

    @Bean
    public CacheManager cacheManager() throws URISyntaxException {
        CacheManagerBuilder.newCacheManagerBuilder() 
        .withCache("preConfigured",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10))) 
            .build();
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager manager = cachingProvider.getCacheManager( 
            getClass().getResource("/ehcache.xml").toURI(), 
            getClass().getClassLoader());
        JCacheCacheManager cacheManager = new JCacheCacheManager(manager);
        return cacheManager;
    }
   
}
