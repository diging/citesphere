package edu.asu.diging.citesphere.core.factory;

import edu.asu.diging.citesphere.model.IUser;
import edu.asu.diging.citesphere.web.forms.UserForm;

public interface IUserFactory {

    IUser createUser(UserForm userForm);

    IUser createUser(String username, String password, String role, boolean enabled);

}