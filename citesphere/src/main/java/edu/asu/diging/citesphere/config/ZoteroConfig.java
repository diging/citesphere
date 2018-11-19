package edu.asu.diging.citesphere.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;

@Configuration
@PropertySource("classpath:/config.properties")
public class ZoteroConfig {

    @Value("${_zotero_client_secret}")
    private String zoteroSecret;
    
    @Value("${_zotero_client_key}")
    private String zoteroKey;

    
    @Bean
    public ZoteroConnectionFactory zoteroConnectionFactory() {
        return new ZoteroConnectionFactory(zoteroKey, zoteroSecret);
    }
}
