package edu.asu.diging.citesphere.core.authority.impl;

import javax.annotation.PostConstruct;

import org.springframework.web.client.RestTemplate;

import edu.asu.diging.citesphere.core.authority.AuthorityImporter;

public abstract class BaseAuthorityImporter implements AuthorityImporter {

    RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();   
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}