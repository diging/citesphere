package edu.asu.diging.citesphere.core.user;
import org.springframework.social.connect.Connection;

public interface IUserHelper {

    public abstract String createUser(Connection<?> connection);

}