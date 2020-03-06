package edu.asu.diging.citesphere.web.user.authorities.helper;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.web.user.FoundAuthorities;

@Service
public class AuthorityEntryControllerHelper {

    @Value("${_conceptpower_authority_search_keyword}")
    private String conceptpowerSearchKeyword;

    public String[] getAuthoritySearchStrings(String firstName, String lastName) {

        String conceptpowerSearchString = "";
        String databaseSearchString = "";

        if (firstName != null && lastName != null) {

            conceptpowerSearchString = conceptpowerSearchKeyword + "%" + firstName + "%" +lastName;

            databaseSearchString = lastName + "%" + firstName;
        } else {

            conceptpowerSearchString = firstName == null ? conceptpowerSearchKeyword + "%" + lastName
                    : conceptpowerSearchKeyword + "%" + firstName;

            databaseSearchString = firstName == null ? lastName : firstName;
        }

        return new String[] { conceptpowerSearchString, databaseSearchString };
    }

    public FoundAuthorities removeDuplicateAuthorities(FoundAuthorities foundAuthorities) {

        ListIterator<IAuthorityEntry> iter = foundAuthorities.getImportedAuthorityEntries().listIterator();

        List<String> uriList = foundAuthorities.getUserAuthorityEntries().stream().map(IAuthorityEntry::getUri)
                .collect(Collectors.toList());

        uriList.addAll(foundAuthorities.getDatasetAuthorityEntries().stream().map(IAuthorityEntry::getUri)
                .collect(Collectors.toList()));

        String uri = "";
        while (iter.hasNext()) {
            uri = iter.next().getUri();
            if (!uri.trim().endsWith("/")) {
                uri = uri + "/";
            }
            if (uriList.contains(uri)) {
                iter.remove();
            }
        }

        return foundAuthorities;
    }
}
