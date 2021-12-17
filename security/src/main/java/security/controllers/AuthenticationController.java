package security.controllers;

import org.bouncycastle.openssl.PasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import security.users.AppUser;

import java.util.List;

@RestController
@RequestMapping("authentication")
public class AuthenticationController {

    private static final String userUrl = "http://eureka-user";

    @Autowired
    private static BCryptPasswordEncoder encoder;

    @Autowired
    private static RestTemplate restTemplate;

    /**
     * Autowired constructor for the class
     *
     * @param restTemplate
     */
    public AuthenticationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static UserDetails loadByUsername(String username, String password) throws UsernameNotFoundException, PasswordException {

        AppUser customer = (AppUser) getCustomerInfo(username);
        AppUser admin = (AppUser) getAdminInfo(username);

        // If user not found. Throw this exception.
        if (customer == null && admin == null) {
            throw new UsernameNotFoundException("Username: " + username + " not found");
        }

        // if admin is not null, it has to be an admin
        if (admin != null) {
            // check whether the password matches
//            if (encoder.matches(password, admin.getPassword())) {
//                throw new PasswordException("Password does not match");
//            }
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList("ROLE_ADMIN");
            return new User(admin.getUsername(), admin.getPassword(), grantedAuthorities);
        }

        // if admin was null then it has to be a customer
        // check whether password matches
//        if (encoder.matches(password, customer.getPassword())) {
//            throw new PasswordException("Password does not match");
//        }
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        return new User(customer.getUsername(), customer.getPassword(), grantedAuthorities);
    }

    public static AppUser getCustomerInfo(@PathVariable String userName){
        String methodSpecificUrl = "/user/" + userName + "/getCustomerInfo";

        AppUser user =
                restTemplate.getForObject(userUrl + methodSpecificUrl, AppUser.class);

        return user;
    }

    public static AppUser getAdminInfo(@PathVariable String userName){
        String methodSpecificUrl = "/user/" + userName + "/getAdminInfo";

        AppUser user =
                restTemplate.getForObject(userUrl + methodSpecificUrl, AppUser.class);

        return user;
    }
}
