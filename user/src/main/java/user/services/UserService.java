package user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import user.config.UserDtoConfig;
import user.entities.Admin;
import user.entities.Customer;
import user.entities.User;
import user.repositories.UserRepository;

@Service
public class UserService {

    private final transient UserRepository<Customer> customerRepository;
    private final transient UserRepository<Admin> adminRepository;

    /**
     * Constructor for UserService.
     *
     * @param customerRepository - retrieves Customer Users from database.
     * @param adminRepository    - retrieves Admin Users from database.
     */
    @Autowired
    public UserService(UserRepository customerRepository, UserRepository adminRepository) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * Finds User by id.
     *
     * @param userId - long
     * @return Optional of User having this id
     */
    public User getUserById(long userId) {
        return customerRepository.findById(userId);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Register customer.
     *
     * @param data the data
     */
    public void registerCustomer(UserDtoConfig data) {
        Customer customer = new Customer();
        customer.setUsername(data.getUsername());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        customer.setPassword(passwordEncoder.encode(data.getPassword()));
        customer.setPremiumUser(data.isPremiumSubscription());
        customerRepository.save(customer);
    }

    /**
     * Register admin.
     *
     * @param data the data
     */
    public void registerAdmin(UserDtoConfig data) {
        Admin admin = new Admin();
        admin.setUsername(data.getUsername());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        admin.setPassword(passwordEncoder.encode(data.getPassword()));
        adminRepository.save(admin);
    }

}
