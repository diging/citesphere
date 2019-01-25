package edu.asu.diging.citesphere.core.authority.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;
import edu.asu.diging.citesphere.core.service.IAuthorityService;

public abstract class BaseAuthorityImporter implements AuthorityImporter {

    @Autowired
    private IAuthorityService authorityService;
    
    RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        authorityService.register(this);        
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }
}