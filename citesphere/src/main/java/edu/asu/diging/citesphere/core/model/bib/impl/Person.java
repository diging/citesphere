package edu.asu.diging.citesphere.core.model.bib.impl;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.asu.diging.citesphere.core.model.bib.IAffiliation;
import edu.asu.diging.citesphere.core.model.bib.IPerson;

@Entity
public class Person implements IPerson, Comparable<Person> {

    @Id
    @GeneratedValue(generator = "person_id_generator")
    @GenericGenerator(name = "person_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "PE"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    @JsonIgnore
    private String id;
    @Lob
    private String name;
    private String uri;
    private String localAuthorityId;
    @Lob
    private String firstName;
    @Lob
    private String lastName;
    private int positionInList;
    
    @OneToMany(targetEntity=Affiliation.class, cascade=CascadeType.ALL)
    @JoinTable(name="Person_Affiliation")
    private Set<IAffiliation> affiliations;
   
    
    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPerson#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPerson#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPerson#getUri()
     */
    @Override
    public String getUri() {
        return uri;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPerson#setUri(java.lang.String)
     */
    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }
    @Override
    public String getLocalAuthorityId() {
        return localAuthorityId;
    }
    @Override
    public void setLocalAuthorityId(String localAuthorityId) {
        this.localAuthorityId = localAuthorityId;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPerson#getFirstName()
     */
    @Override
    public String getFirstName() {
        return firstName;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPerson#setFirstName(java.lang.String)
     */
    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPerson#getLastName()
     */
    @Override
    public String getLastName() {
        return lastName;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.IPerson#setLastName(java.lang.String)
     */
    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @Override
    public int getPositionInList() {
        return positionInList;
    }
    @Override
    public void setPositionInList(int order) {
        this.positionInList = order;
    }
    @Override
    public Set<IAffiliation> getAffiliations() {
        return affiliations;
    }
    @Override
    public void setAffiliations(Set<IAffiliation> affiliations) {
        this.affiliations = affiliations;
    }
    
    @Override
    public int compareTo(Person o) {
        int compared = getPositionInList() - o.getPositionInList();
        if (compared == 0) {
            return getName().compareTo(o.getName());
        }
        return compared;
    }
    
}
