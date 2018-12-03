package edu.asu.diging.citesphere.core.user;

import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.UserAlreadyExistsException;
import edu.asu.diging.citesphere.core.model.IUser;

public interface IUserManager {

    void create(IUser user) throws UserAlreadyExistsException;

    IUser findByUsername(String username);

    List<IUser> findAll();

    void approveAccount(String username, String approver);

}