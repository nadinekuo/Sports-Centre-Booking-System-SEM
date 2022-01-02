package user.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import user.entities.Admin;
import user.entities.Customer;
import user.entities.User;

@Repository
public interface UserRepository<T extends User> extends JpaRepository<T, Long> {

    /**
     * See if there is a user matching the given userId.
     *
     * @param userId the userId
     * @return the optional user
     */
    T findById(long userId);

    /**
     * Delete a user matching the given userId.
     *
     * @param userId the userId
     * @return void
     */
    @Transactional
    void deleteById(Long userId);

    /**
     * See if there is a user matching the given username.
     *
     * @param username the username
     * @return the optional user
     */
    Optional<T> findByUsername(String username);

    /**
     * See if there is a customer matching the given username.
     *
     * @param username the username
     * @return the optional customer
     */
    @Query(value = "SELECT * " + "FROM customers "
            + "WHERE username = ?1", nativeQuery = true)
    Optional<Customer> findCustomerByUsername(String username);

    /**
     * See if there is an admin matching the given username.
     *
     * @param username the username
     * @return the optional admin
     */
    @Query(value = "SELECT * " + "FROM admins "
            + "WHERE username = ?1", nativeQuery = true)
    Optional<Admin> findAdminByUsername(String username);
}
