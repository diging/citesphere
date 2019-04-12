package edu.asu.diging.citesphere.web.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import edu.asu.diging.citesphere.web.validation.FieldMatch;

@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match.")
public class UserForm {
    
    @NotEmpty(message="Please enter a username.")
    private String username;
    private String firstName;
    private String lastName;
    
    @Email(message="Please provide a well-formed email address.")
    @NotEmpty(message="Please enter your email address.")
    private String email;
    @NotEmpty(message="Please enter a password.")
    private String password;
    @NotEmpty(message="Please repeat your password.")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
}
