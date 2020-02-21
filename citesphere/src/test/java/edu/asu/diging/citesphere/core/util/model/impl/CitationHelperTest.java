package edu.asu.diging.citesphere.core.util.model.impl;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.*;

import edu.asu.diging.citesphere.core.exceptions.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.service.impl.CitationManager;
import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.model.bib.IAffiliation;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationConcept;
import edu.asu.diging.citesphere.model.bib.ICitationConceptTag;
import edu.asu.diging.citesphere.model.bib.IConceptType;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.impl.Affiliation;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.CitationConcept;
import edu.asu.diging.citesphere.model.bib.impl.CitationConceptTag;
import edu.asu.diging.citesphere.model.bib.impl.ConceptType;
import edu.asu.diging.citesphere.model.bib.impl.Person;
import edu.asu.diging.citesphere.model.impl.User;
import edu.asu.diging.citesphere.web.forms.AffiliationForm;
import edu.asu.diging.citesphere.web.forms.CitationForm;
import edu.asu.diging.citesphere.web.forms.ConceptAssignmentForm;
import edu.asu.diging.citesphere.web.forms.PersonForm;

public class CitationHelperTest {

    @Mock
    private ICitationConceptManager conceptManager;

    @Mock
    private IConceptTypeManager typeManager;

    ICitation citation;
    ICitation updatedCitation;
    CitationForm form;
    IUser user;

    @InjectMocks
    private CitationHelper helperToTest;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setUsername("akhil");

        initForm();
        initUpdatedCitation();

    }

    private void initForm() {
        form = new CitationForm();
        form.setTitle("updatedtitle");
        form.setShortTitle("updatedShortTitle");
        List<PersonForm> authors = new ArrayList<PersonForm>();
        PersonForm author = new PersonForm();
        author.setFirstName("first");
        author.setLastName("last");

        List<AffiliationForm> affiliationForms = new ArrayList<AffiliationForm>();
        AffiliationForm affiliationForm = new AffiliationForm();
        affiliationForm.setName("Test");
        affiliationForms.add(affiliationForm);
        author.setAffiliations(affiliationForms);

        authors.add(author);

        PersonForm author2 = new PersonForm();
        author2.setFirstName("first2");
        author2.setLastName("last2");
        authors.add(author2);
        form.setAuthors(authors);

        form.setDateFreetext("2019");
        form.setVolume("1.0");

        List<PersonForm> editors = new ArrayList<PersonForm>();
        PersonForm editor = new PersonForm();
        editor.setFirstName("editor");
        editor.setLastName("editor");

        editors.add(editor);
        form.setEditors(editors);

        List<ConceptAssignmentForm> conceptTags = new ArrayList<>();
        ConceptAssignmentForm tag = new ConceptAssignmentForm();
        tag.setConceptName("testc");
        tag.setConceptUri("google.com");
        tag.setConceptId("CON1");
        tag.setConceptTypeId("CTY1");
        tag.setConceptTypeName("forum");
        tag.setConceptTypeUri("test.com");
        conceptTags.add(tag);
        form.setConceptTags(conceptTags);
    }

    private void initUpdatedCitation() {
        updatedCitation = new Citation();
        updatedCitation.setTitle("updatedtitle");
        updatedCitation.setShortTitle("updatedShortTitle");
        updatedCitation.setDateFreetext("2019");
        updatedCitation.setVolume("1.0");

        Set<IPerson> editorsSet = new HashSet<IPerson>();
        IPerson editor1 = new Person();
        editor1.setFirstName("editor");
        editor1.setLastName("editor");
        editor1.setId("1");
        editorsSet.add(editor1);
        updatedCitation.setEditors(editorsSet);

        Set<IPerson> authorsSet = new HashSet<IPerson>();
        IPerson author1 = new Person();
        author1.setFirstName("first");
        author1.setLastName("last");
        author1.setId("2");

        Set<IAffiliation> affiliations = new HashSet<IAffiliation>();
        IAffiliation affiliation = new Affiliation();
        affiliation.setName("Test");
        affiliations.add(affiliation);
        author1.setAffiliations(affiliations);
        authorsSet.add(author1);

        IPerson author2 = new Person();
        author2.setFirstName("first2");
        author2.setLastName("last2");

        authorsSet.add(author2);

        updatedCitation.setAuthors(authorsSet);

        Set<ICitationConceptTag> conceptTagsSet = new HashSet<ICitationConceptTag>();
        ICitationConceptTag tag1 = new CitationConceptTag();
        tag1.setConceptName("testc");
        tag1.setConceptUri("google.com");
        tag1.setLocalConceptId("CON1");
        tag1.setLocalConceptTypeId("CTY1");
        tag1.setTypeName("forum");
        tag1.setTypeUri("test.com");
        conceptTagsSet.add(tag1);
        updatedCitation.setConceptTags(conceptTagsSet);
    }

    @Test
    public void test_updateCitation_success() {
        citation = new Citation();
        citation.setTitle("title");
        citation.setShortTitle("shortTitle");
        citation.setDateFreetext("2019");
        citation.setVolume("1.0");
        ICitationConcept concept = new CitationConcept();
        concept.setId("CON1");
        concept.setOwner(user);
        concept.setUri("google.com");

        IConceptType type = new ConceptType();
        type.setId("CTY1");
        type.setOwner(user);
        type.setName("forum");
        type.setUri("test.com");
        Set<IPerson> authorsSet = new HashSet<IPerson>();
        IPerson author2 = new Person();
        author2.setFirstName("first2");
        author2.setLastName("last2");

        authorsSet.add(author2);
        citation.setAuthors(authorsSet);

        Mockito.when(conceptManager.save(any(ICitationConcept.class))).thenReturn(concept);
        Mockito.when(typeManager.save(any(IConceptType.class))).thenReturn(type);
        helperToTest.updateCitation(citation, form, user);
        assertTrue(equalsCitation(citation, updatedCitation));
    }

    private boolean equalsCitation(ICitation c1, ICitation c2) {
        // TODO Auto-generated method stub

        if (!c1.getTitle().equals(c2.getTitle()))
            return false;
        if (!c1.getShortTitle().equals(c2.getShortTitle()))
            return false;
        if (c1.getAuthors() != null && c2.getAuthors() != null) {

            if (c1.getAuthors().size() != c2.getAuthors().size())
                return false;
            Iterator<IPerson> p1 = c1.getAuthors().iterator();
            Iterator<IPerson> p2 = c2.getAuthors().iterator();
            while (p1.hasNext() && p2.hasNext()) {
                if (!comparePerson(p1.next(), p2.next()))
                    return false;
            }
        }

        if (c1.getEditors() != null && c2.getEditors() != null) {

            if (c1.getEditors().size() != c2.getEditors().size())
                return false;
            Iterator<IPerson> p1 = c1.getEditors().iterator();
            Iterator<IPerson> p2 = c2.getEditors().iterator();
            while (p1.hasNext() && p2.hasNext()) {
                if (!comparePerson(p1.next(), p2.next()))
                    return false;
            }
        }

        if (c1.getOtherCreators() != null && c2.getOtherCreators() != null) {

            if (c1.getOtherCreators().size() != c2.getOtherCreators().size())
                return false;
            Iterator<ICreator> p1 = c1.getOtherCreators().iterator();
            Iterator<ICreator> p2 = c2.getOtherCreators().iterator();
            while (p1.hasNext() && p2.hasNext()) {
                if (!comparePerson(p1.next().getPerson(), p2.next().getPerson()))
                    return false;
            }

        }

        if (c1.getConceptTags() != null && c2.getConceptTags() != null) {

            if (c1.getConceptTags().size() != c2.getConceptTags().size())
                return false;
            Iterator<ICitationConceptTag> p1 = c1.getConceptTags().iterator();
            Iterator<ICitationConceptTag> p2 = c2.getConceptTags().iterator();
            while (p1.hasNext() && p2.hasNext()) {
                if (!compareConceptTag(p1.next(), p2.next()))
                    return false;
            }
        }
        return true;
    }

    private boolean comparePerson(IPerson p1, IPerson p2) {

        if (!p1.getFirstName().equals(p2.getFirstName()) || !p1.getLastName().equals(p2.getLastName()))
            return false;
        if (p1.getAffiliations() != null && p2.getAffiliations() != null) {
            if (p1.getAffiliations().size() != p2.getAffiliations().size())
                return false;
            Iterator<IAffiliation> i1 = p1.getAffiliations().iterator();
            Iterator<IAffiliation> i2 = p2.getAffiliations().iterator();
            while (i1.hasNext() && i2.hasNext()) {
                if (!compareAffliattion(i1.next(), i2.next()))
                    return false;
            }
        }

        return true;
    }

    private boolean compareAffliattion(IAffiliation a1, IAffiliation a2) {
        // TODO Auto-generated method stub

        if (a1.getName().equals(a2.getName()))
            return true;

        return false;

    }

    private boolean compareConceptTag(ICitationConceptTag c1, ICitationConceptTag c2) {

        if (c1.getLocalConceptId().equals(c2.getLocalConceptId())
                && c1.getLocalConceptTypeId().equals(c2.getLocalConceptTypeId()))
            return true;

        return false;
    }

}
