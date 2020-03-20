package edu.asu.diging.citesphere.core.util.model.impl;

import java.util.*;

import org.apache.xml.utils.URI;
import org.hamcrest.Matcher;
import org.hibernate.validator.constraints.URL;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.util.UriBuilder;

import static org.junit.Assert.*;
import edu.asu.diging.citesphere.core.service.ICitationConceptManager;
import edu.asu.diging.citesphere.core.service.IConceptTypeManager;
import edu.asu.diging.citesphere.core.service.impl.CitationManager;
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
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;
import edu.asu.diging.citesphere.web.forms.AffiliationForm;
import edu.asu.diging.citesphere.web.forms.CitationForm;
import edu.asu.diging.citesphere.web.forms.ConceptAssignmentForm;
import edu.asu.diging.citesphere.web.forms.PersonForm;

public class CitationHelperTest {
    @InjectMocks
    private CitationHelper helperToTest;

    @Mock
    private ICitationConceptManager conceptManager;

    @Mock
    private IConceptTypeManager typeManager;

    private ICitation citation;

    private ICitation updatedCitation;

    private CitationForm form;

    private IUser user;

    private ICitationConcept concept;

    private IConceptType type;

    private static final String CONCEPT_ID = "CON1";

    private static final String TYPE_ID = "CTY1";

    private static final String FORM_YEAR = "2019";

    private static final String CONCEPT_NAME = "testc";

    private static final String TYPE_NAME = "forum";

    private static final String CONCEPT_URI = "www.google.com";

    private static final String TYPE_URI = "www.test.com";

    private static final String FORM_TITLE = "updatedtitle";

    private static final String FORM_SHORT_TITLE = "updatedShortTitle";

    private static final String CITATION_SHORT_TITLE = "Name";

    private static final String CITATION_TITLE = "Name";

    private static final String FORM_VOLUME = "1.0";

    private static final String FORM_PERSON_NAME = "Name";

    private static final String FORM_AFFILIATION_NAME = "Name";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setUsername("akhil");

        initForm();
        initUpdatedCitation();
        initCitation();
    }

    private void initCitation() {
        citation = new Citation();
        citation.setTitle(CITATION_TITLE);
        citation.setShortTitle(CITATION_SHORT_TITLE);
        citation.setDateFreetext(FORM_YEAR);
        citation.setVolume(FORM_VOLUME);

        concept = new CitationConcept();
        concept.setId(CONCEPT_ID);
        concept.setOwner(user);
        concept.setName(CONCEPT_NAME);
        concept.setUri(CONCEPT_URI);

        type = new ConceptType();
        type.setId(TYPE_ID);
        type.setOwner(user);
        type.setName(TYPE_NAME);
        type.setUri(TYPE_URI);
    }

    private void initForm() {
        form = new CitationForm();
        form.setTitle(FORM_TITLE);
        form.setShortTitle(FORM_SHORT_TITLE);

        List<PersonForm> authors = new ArrayList<PersonForm>();
        PersonForm author = new PersonForm();
        author.setFirstName(FORM_PERSON_NAME);
        author.setLastName(FORM_PERSON_NAME);

        List<AffiliationForm> affiliationForms = new ArrayList<AffiliationForm>();
        AffiliationForm affiliationForm = new AffiliationForm();
        affiliationForm.setName(FORM_AFFILIATION_NAME);
        affiliationForms.add(affiliationForm);
        author.setAffiliations(affiliationForms);
        authors.add(author);

        form.setAuthors(authors);
        form.setDateFreetext(FORM_YEAR);
        form.setVolume(FORM_VOLUME);

        List<PersonForm> editors = new ArrayList<PersonForm>();
        PersonForm editor = new PersonForm();
        editor.setFirstName(FORM_PERSON_NAME);
        editor.setLastName(FORM_PERSON_NAME);
        editors.add(editor);
        form.setEditors(editors);

        List<ConceptAssignmentForm> conceptTags = new ArrayList<>();
        ConceptAssignmentForm tag = new ConceptAssignmentForm();
        tag.setConceptName(CONCEPT_NAME);
        tag.setConceptUri(CONCEPT_URI);
        tag.setConceptId(CONCEPT_ID);
        tag.setConceptTypeId(TYPE_ID);
        tag.setConceptTypeName(TYPE_NAME);
        tag.setConceptTypeUri(TYPE_URI);
        conceptTags.add(tag);
        form.setConceptTags(conceptTags);
    }

    private void initUpdatedCitation() {
        updatedCitation = new Citation();
        updatedCitation.setTitle(FORM_TITLE);
        updatedCitation.setShortTitle(FORM_SHORT_TITLE);
        updatedCitation.setDateFreetext(FORM_YEAR);
        updatedCitation.setVolume(FORM_VOLUME);

        Set<IPerson> editorsSet = new LinkedHashSet<IPerson>();
        IPerson editor1 = new Person();
        editor1.setFirstName(FORM_PERSON_NAME);
        editor1.setLastName(FORM_PERSON_NAME);
        editor1.setId("1");
        editorsSet.add(editor1);
        updatedCitation.setEditors(editorsSet);

        Set<IPerson> authorsSet = new LinkedHashSet<IPerson>();
        IPerson author1 = new Person();
        author1.setFirstName(FORM_PERSON_NAME);
        author1.setLastName(FORM_PERSON_NAME);
        author1.setId("2");
        Set<IAffiliation> affiliations = new LinkedHashSet<IAffiliation>();
        IAffiliation affiliation = new Affiliation();
        affiliation.setName(FORM_AFFILIATION_NAME);
        affiliations.add(affiliation);
        author1.setAffiliations(affiliations);
        authorsSet.add(author1);
        updatedCitation.setAuthors(authorsSet);

        Set<ICitationConceptTag> conceptTagsSet = new LinkedHashSet<ICitationConceptTag>();
        ICitationConceptTag tag1 = new CitationConceptTag();
        tag1.setConceptName(CONCEPT_NAME);
        tag1.setConceptUri(CONCEPT_URI);
        tag1.setLocalConceptId(CONCEPT_ID);
        tag1.setLocalConceptTypeId(TYPE_ID);
        tag1.setTypeName(TYPE_NAME);
        tag1.setTypeUri(TYPE_URI);
        conceptTagsSet.add(tag1);

        updatedCitation.setConceptTags(conceptTagsSet);
        updatedCitation.setOtherCreators(new HashSet<>());
    }

    @Test
    public void test_updateCitation() {
        Mockito.when(conceptManager.save(Mockito.argThat(new ConceptArgMatcher(concept)))).thenReturn(concept);
        Mockito.when(typeManager.save(Mockito.argThat(new ConceptTypeArgMatcher(type)))).thenReturn(type);
        helperToTest.updateCitation(citation, form, user);
        assertTrue(equalsCitation(citation, updatedCitation));
    }

    @Test
    public void test_updateCitation_getByURIAndOwner() {
        Mockito.when(conceptManager.getByUriAndOwner(CONCEPT_URI, user)).thenReturn(concept);
        Mockito.when(typeManager.getByUriAndOwner(TYPE_URI, user)).thenReturn(type);
        helperToTest.updateCitation(citation, form, user);
        assertTrue(equalsCitation(citation, updatedCitation));
    }

    @Test(expected = NullPointerException.class)
    public void test_updateCitation_getByURIAndOwner_Invalid_URI() {
        Mockito.when(conceptManager.getByUriAndOwner("www.google1.com", user)).thenReturn(concept);
        Mockito.when(typeManager.getByUriAndOwner("www.test1.com", user)).thenReturn(type);
        helperToTest.updateCitation(citation, form, user);
        equalsCitation(citation, updatedCitation);
    }

    private boolean comparePersons(Set<IPerson> a1, Set<IPerson> a2) {
        if (a1 != null && a2 != null) {
            if (a1.size() != a2.size()) {
                return false;
            }
            Iterator<IPerson> p1 = a1.iterator();
            Iterator<IPerson> p2 = a2.iterator();
            while (p1.hasNext() && p2.hasNext()) {
                if (!comparePerson(p1.next(), p2.next())) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean compareCreators(Set<ICreator> a1, Set<ICreator> a2) {
        if (a1 != null && a2 != null) {
            if (a1.size() != a2.size()) {
                return false;
            }
            Iterator<ICreator> p1 = a1.iterator();
            Iterator<ICreator> p2 = a1.iterator();
            while (p1.hasNext() && p2.hasNext()) {
                if (!comparePerson(p1.next().getPerson(), p2.next().getPerson())) {
                    return false;
                }
            }
            return true;

        }

        return false;
    }

    private boolean compareTags(Set<ICitationConceptTag> a1, Set<ICitationConceptTag> a2) {
        if (a1 != null && a2 != null) {
            if (a1.size() != a2.size()) {
                return false;
            }
            Iterator<ICitationConceptTag> p1 = a1.iterator();
            Iterator<ICitationConceptTag> p2 = a2.iterator();
            while (p1.hasNext() && p2.hasNext()) {
                if (!compareConceptTag(p1.next(), p2.next())) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean equalsCitation(ICitation c1, ICitation c2) {
        if (!c1.getTitle().equals(c2.getTitle())) {
            return false;
        }

        if (!c1.getShortTitle().equals(c2.getShortTitle())) {
            return false;
        }

        if (!comparePersons(c1.getAuthors(), c2.getAuthors())) {
            return false;
        }

        if (!comparePersons(c1.getEditors(), c2.getEditors())) {
            return false;
        }

        if (!compareCreators(c1.getOtherCreators(), c2.getOtherCreators())) {
            return false;
        }

        if (!compareTags(c1.getConceptTags(), c2.getConceptTags())) {
            return false;
        }

        return true;
    }

    private boolean comparePerson(IPerson p1, IPerson p2) {
        if (!p1.getFirstName().equals(p2.getFirstName()) || !p1.getLastName().equals(p2.getLastName())) {
            return false;
        }
        if (p1.getAffiliations() != null && p2.getAffiliations() != null) {
            if (p1.getAffiliations().size() != p2.getAffiliations().size())
                return false;
            Iterator<IAffiliation> i1 = p1.getAffiliations().iterator();
            Iterator<IAffiliation> i2 = p2.getAffiliations().iterator();
            while (i1.hasNext() && i2.hasNext()) {
                if (!compareAffliattion(i1.next(), i2.next())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean compareAffliattion(IAffiliation a1, IAffiliation a2) {
        if (a1.getName().equals(a2.getName())) {
            return true;
        }

        return false;
    }

    private boolean compareConceptTag(ICitationConceptTag c1, ICitationConceptTag c2) {
        if (c1.getLocalConceptId().equals(c2.getLocalConceptId())
                && c1.getLocalConceptTypeId().equals(c2.getLocalConceptTypeId())) {
            return true;
        }

        return false;
    }

    class ConceptArgMatcher extends ArgumentMatcher<ICitationConcept> {

        private ICitationConcept conceptToBeTested;

        public ConceptArgMatcher(ICitationConcept concept) {
            conceptToBeTested = concept;
        }

        @Override
        public boolean matches(Object arg) {
            ICitationConcept conceptArg = (ICitationConcept) arg;

            if (!conceptArg.getName().equals(conceptToBeTested.getName())) {
                return false;
            }

            if (!conceptArg.getUri().equals(conceptToBeTested.getUri())) {
                return false;
            }

            if (!conceptArg.getOwner().equals(conceptToBeTested.getOwner())) {
                return false;
            }

            return true;
        }
    }

    class ConceptTypeArgMatcher extends ArgumentMatcher<IConceptType> {

        private IConceptType conceptTypeToBeTested;

        public ConceptTypeArgMatcher(IConceptType type) {
            conceptTypeToBeTested = type;
        }

        @Override
        public boolean matches(Object arg) {
            IConceptType conceptTypeArg = (IConceptType) arg;

            if (!conceptTypeArg.getName().equals(conceptTypeToBeTested.getName())) {
                return false;
            }

            if (!conceptTypeArg.getUri().equals(conceptTypeToBeTested.getUri())) {
                return false;
            }

            if (!conceptTypeArg.getOwner().equals(conceptTypeToBeTested.getOwner())) {
                return false;
            }

            return true;
        }
    }
}
