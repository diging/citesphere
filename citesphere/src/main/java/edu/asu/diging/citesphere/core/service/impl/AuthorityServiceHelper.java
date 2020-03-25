package edu.asu.diging.citesphere.core.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceHelper {

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

    public String getViafSearchString(String firstName, String lastName) {
        return " " + firstName + " " + lastName;
    }
}
