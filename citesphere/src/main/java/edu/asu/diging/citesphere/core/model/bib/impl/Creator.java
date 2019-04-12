package edu.asu.diging.citesphere.core.model.bib.impl;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.asu.diging.citesphere.core.model.bib.ICreator;
import edu.asu.diging.citesphere.core.model.bib.IPerson;

@Entity
public class Creator implements ICreator, Comparable<ICreator> {

    @Id
    @GeneratedValue(generator = "creator_id_generator")
    @GenericGenerator(name = "creator_id_generator",    
                    parameters = @Parameter(name = "prefix", value = "CR"), 
                    strategy = "edu.asu.diging.citesphere.core.repository.IdGenerator"
            )
    @JsonIgnore
    private String id;
    
    private String role;
    @OneToOne(targetEntity=Person.class, cascade=CascadeType.ALL)
    @JoinColumn(name="person_id")
    private IPerson person;
    
    // not ideal but ah well, crappy data model
    private int positionInList;
    
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICreator#getRole()
     */
    @Override
    public String getRole() {
        return role;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICreator#setRole(java.lang.String)
     */
    @Override
    public void setRole(String role) {
        this.role = role;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICreator#getPerson()
     */
    @Override
    public IPerson getPerson() {
        return person;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.core.model.bib.impl.ICreator#setPerson(edu.asu.diging.citesphere.core.model.bib.IPerson)
     */
    @Override
    public void setPerson(IPerson person) {
        this.person = person;
    }
    @Override
    public int getPositionInList() {
        return positionInList;
    }
    @Override
    public void setPositionInList(int positionInList) {
        this.positionInList = positionInList;
    }
    @Override
    public int compareTo(ICreator o) {
        int compared = getRole().compareTo(o.getRole());
        if (compared == 0) {
            compared = getPositionInList() - o.getPositionInList();
            if (compared == 0) {
                return person.getName().compareTo(o.getPerson().getName());
            }
        }
        return compared;
    }
}
