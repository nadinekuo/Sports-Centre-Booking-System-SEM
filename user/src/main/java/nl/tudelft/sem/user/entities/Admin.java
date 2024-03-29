package nl.tudelft.sem.user.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User {

    public Admin() {
    }

    /**
     * Constructor with id.
     *
     * @param id       - long
     * @param username - String
     * @param password - String
     */
    public Admin(long id, String username, String password) {

        super(id, username, password);
    }

    /**
     * Initiates a new Admin.
     *
     * @param username the username
     * @param password the password
     */
    public Admin(String username, String password) {
        super(username, password);
    }

    /**
     * The overridden toString method.
     *
     * @return String representation of the admin object
     */
    @Override
    public String toString() {
        return "Admin{" + "id=" + super.getId() + ", username='" + super.getUsername() + '\''
            + ", password" + "='" + super.getPassword() + "'}";
    }

}
