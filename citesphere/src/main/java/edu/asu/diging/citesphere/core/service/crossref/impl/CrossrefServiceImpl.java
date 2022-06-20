package edu.asu.diging.citesphere.core.service.crossref.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.core.service.crossref.CrossrefService;
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.service.CrossrefConfiguration;
import edu.asu.diging.crossref.service.CrossrefWorksService;
import edu.asu.diging.crossref.service.impl.CrossrefWorksServiceImpl;

@Service
@PropertySource("config.properties")
public class CrossrefServiceImpl implements CrossrefService {
    
    @Value("${_crossref_default_pagesize}")
    private int defaultPageSize;

    private CrossrefWorksService service;
    
    @PostConstruct
    public void init() {
        service = new CrossrefWorksServiceImpl(CrossrefConfiguration.getDefaultConfig());
    }
          

    @Override
    public List<Item> search(String query, int page) throws RequestFailedException, IOException  {
        return service.search(query, defaultPageSize, (page-1)*defaultPageSize);
    }
}
