package edu.asu.diging.citesphere.core.authority.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ViafSearchResponseWrapper {

    private ViafSearchResponse searchRetrieveResponse;

    public ViafSearchResponse getSearchRetrieveResponse() {
        return searchRetrieveResponse;
    }

    public void setSearchRetrieveResponse(ViafSearchResponse searchRetrieveResponse) {
        this.searchRetrieveResponse = searchRetrieveResponse;
    }
    
    public static class ViafSearchResponse {
        private int numberOfRecords;
        private List<ViafSearchItem> records;

        public int getNumberOfRecords() {
            return numberOfRecords;
        }

        public void setNumberOfRecords(int numberOfRecords) {
            this.numberOfRecords = numberOfRecords;
        }

        public List<ViafSearchItem> getRecords() {
            return records;
        }

        public void setRecords(List<ViafSearchItem> records) {
            this.records = records;
        }
    }
    
    public static class ViafSearchItem {
        private ViafSearchRecord record;

        public ViafSearchRecord getRecord() {
            return record;
        }

        public void setRecord(ViafSearchRecord record) {
            this.record = record;
        }
    }
    
    public static class ViafSearchRecord {
        private ViafRecordData recordData;

        public ViafRecordData getRecordData() {
            return recordData;
        }

        public void setRecordData(ViafRecordData recordData) {
            this.recordData = recordData;
        }
    }
    
}
