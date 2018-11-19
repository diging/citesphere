package edu.asu.diging.citesphere.core.zotero.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:config.properties")
public class ZoteroConnector {
    
    @Value("${_zotero_client_secret}")
    private String zoteroSecret;
    
    @Value("${_zotero_client_key}")
    private String zoteroKey;
    
   
}
