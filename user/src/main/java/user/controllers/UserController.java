package user.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import user.config.UserDtoConfig;
import user.entities.Admin;
import user.entities.Customer;
import user.entities.User;
import user.services.UserService;

@RestController
@RequestMapping("user")
public class UserController {

    private final transient UserService userService;

    @Autowired
    private final transient RestTemplate restTemplate;

    /**
     * Autowired constructor for the class.
     *
     * @param userService userService
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.restTemplate = userService.restTemplate();
    }

    /**
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/isPremium")
    @ResponseBody
    public ResponseEntity<String> isUserPremium(@PathVariable Long userId) {
        try {
            Customer customer = (Customer) userService.getUserById(userId);
            Boolean isPremium = customer.isPremiumUser();
            return new ResponseEntity<String>(isPremium.toString(), HttpStatus.OK);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            System.out.println("User with id " + userId + " does not exist!!");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userName}/getCustomerInfo")
    @ResponseBody
    public User getCustomerInfo(@PathVariable String userName) {
        Customer customer = userService.getCustomerByUsername(userName);
        return customer;
    }

    @GetMapping("/{userName}/getAdminInfo")
    @ResponseBody
    public User getAdminInfo(@PathVariable String userName) {
        Admin admin = userService.getAdminByUsername(userName);
        return admin;
    }

    /**
     * Customer registration.
     *
     * @param request the request to register a user.
     * @throws IOException If customer can't be registered
     */
    @PostMapping("/registerCustomer")
    public void customerRegistration(HttpServletRequest request) throws IOException {
        UserDtoConfig data =
            new ObjectMapper().readValue(request.getInputStream(), UserDtoConfig.class);
        userService.registerCustomer(data);
    }

    /**
     * Admin registration.
     *
     * @param request the request to register an admin
     * @throws IOException if admin can't be registered
     */
    @PostMapping("/registerAdmin/admin")
    public void adminRegistration(HttpServletRequest request) throws IOException {
        UserDtoConfig data =
            new ObjectMapper().readValue(request.getInputStream(), UserDtoConfig.class);
        userService.registerAdmin(data);
    }

}
