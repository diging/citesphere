package edu.asu.diging.citesphere.core.export;

public enum ExportType {
    CSV("csv");
    
    private String fileExtension;
    
    private ExportType(String ext) {
        this.fileExtension = ext;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
}
