package edu.asu.diging.citesphere.core.user;

import java.util.List;

import edu.asu.diging.citesphere.core.exceptions.UserAlreadyExistsException;
import edu.asu.diging.citesphere.core.exceptions.UserDoesNotExistException;
import edu.asu.diging.citesphere.core.model.Role;
import edu.asu.diging.citesphere.model.IUser;

public interface IUserManager {

    void create(IUser user) throws UserAlreadyExistsException;

    IUser findByUsername(String username);

    List<IUser> findAll();

    void approveAccount(String username, String approver);

    /**
     * Adds given role to a user. 
     * @param username Username of user to be changed.
     * @param initiator Username of user initiating the changne.
     * @param role Name of role to be added. Use roles defined in {@link Role}.
     */
    void addRole(String username, String initiator, String role);

    void removeRole(String username, String initiator, String role);

    void disableUser(String username, String initiator);

    IUser findByEmail(String email);

    void changePassword(IUser user, String password) throws UserDoesNotExistException;

}