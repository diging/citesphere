package edu.asu.diging.citesphere.core.model.bib;

public interface ICreator {

    String getRole();

    void setRole(String role);

    IPerson getPerson();

    void setPerson(IPerson person);

    void setPositionInList(int positionInList);

    int getPositionInList();

    String getId();
    
    void setId(String id);
    
}