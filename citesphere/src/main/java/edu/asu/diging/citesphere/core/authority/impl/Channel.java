package edu.asu.diging.citesphere.core.authority.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * jaxb class for parsing the xml returned by viaf service
 * 
 * @author rohit
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Channel {
    
    @XmlElement(name = "item")
    private List<Item> items;
    
    @XmlElement(name = "title")
    private String title;
    
    @XmlElement(name = "totalResults", namespace = "http://a9.com/-/spec/opensearch/1.1/")
    private Integer totalResults;
    
    @XmlElement(name = "link")
    private String link;
    
    @XmlElement(name = "description")
    private String description;
    
    public Integer getTotalResults() {
        return totalResults;
    }
    
    public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}
    
    public String getTitle() {
		return title;
	}
    
    public void setTitle(String title) {
		this.title = title;
	}
    
    public String getLink() {
		return link;
	}
    
    public void setLink(String link) {
		this.link = link;
	}
    
    public String getDescription() {
		return description;
	}
    
    public void setDescription(String description) {
		this.description = description;
	}
    
    public List<Item> getItems() {
		return items;
	}
    
    public void setItems(List<Item> item) {
		this.items = item;
	}
	
}
