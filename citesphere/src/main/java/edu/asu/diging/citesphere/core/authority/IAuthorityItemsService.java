package edu.asu.diging.citesphere.core.authority;

import edu.asu.diging.citesphere.model.authority.IAuthorityEntry;
import edu.asu.diging.citesphere.model.transfer.impl.Citations;

public interface IAuthorityItemsService {

    public Citations findAuthorityItems(IAuthorityEntry entry);

}