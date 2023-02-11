package edu.asu.diging.citesphere.core.authority.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IsiscbResponse {

    private List<IsiscbEntry> results;

    private int count;

    public List<IsiscbEntry> getIsiscbEntries() {
        return results;
    }

    public void setIsiscbEntries(List<IsiscbEntry> results) {
        this.results = results;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
