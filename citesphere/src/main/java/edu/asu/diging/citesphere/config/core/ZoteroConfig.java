package edu.asu.diging.citesphere.config.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;
import edu.asu.diging.citesphere.core.zotero.custom.CustomZoteroConnectionFactory;

@Configuration
@PropertySource("classpath:/config.properties")
public class ZoteroConfig {

    @Value("${_zotero_client_secret}")
    private String zoteroSecret;
    
    @Value("${_zotero_client_key}")
    private String zoteroKey;

    
    @Bean
    public CustomZoteroConnectionFactory zoteroConnectionFactory(
            @Value("${_zotero_client_key}") String zoteroKey,
            @Value("${_zotero_client_secret}") String zoteroSecret) {
        return new CustomZoteroConnectionFactory(zoteroKey, zoteroSecret);
    }    
}
