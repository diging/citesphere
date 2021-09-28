package edu.asu.diging.citesphere.web.user;

public class AvailableColumnsDataDto {
    private String column;
    private String columnLabel;
    
    public AvailableColumnsDataDto (String column, String columnLabel) {
        this.column = column;
        this.columnLabel = columnLabel;
    }
    
    public String getColumn() {
        return column;
    }
    
    public void setColumn(String column) {
        this.column = column;
    }
    
    public String getColumnLabel() {
        return columnLabel;
    }
    
    public void setColumnLabel(String columnLabel) {
        this.columnLabel = columnLabel;
    }
}
