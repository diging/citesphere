package edu.asu.diging.citesphere.web.user.authorities.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthorityEntryControllerHelper {

    @Value("${_conceptpower_authority_search_keyword}")
    private String conceptpowerSearchKeyword;

    public String getConceptpowerSearchString(String firstName, String lastName) {

        String conceptpowerSearchString = "";

        if (firstName != null && lastName != null) {

            conceptpowerSearchString = conceptpowerSearchKeyword + "%" + firstName + "%" + lastName;

        } else {

            conceptpowerSearchString = firstName == null ? conceptpowerSearchKeyword + "%" + lastName
                    : conceptpowerSearchKeyword + "%" + firstName;

        }

        return conceptpowerSearchString;
    }
}
