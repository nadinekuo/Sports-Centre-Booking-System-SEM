package nl.tudelft.sem.security.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import nl.tudelft.sem.security.controllers.AuthenticationController;
import nl.tudelft.sem.security.users.AppUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserDetailsServiceImplTest {

    UserDetailsServiceImpl userDetailsService;
    String username;
    String password;
    String userRole;
    String adminRole;
    MockedStatic<AuthenticationController> controller;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl();
        username = "erwin";
        password = "password";
        userRole = "user";
        adminRole = "admin";
        controller = Mockito.mockStatic(AuthenticationController.class);
    }

    @AfterEach
    void close() {
        controller.close();
    }

    @Test
    void loadUserByUsernameTest() {
        List<GrantedAuthority> grantedAuthoritiesCustomer =
            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
        controller.when(() -> AuthenticationController.getCustomerInfo(username))
            .thenReturn(new AppUser(username, password, userRole));
        assertEquals(userDetailsService.loadUserByUsername(username),
            new User(username, password, grantedAuthoritiesCustomer));
    }

    @Test
    void loadAdminByUsernameTest() {
        List<GrantedAuthority> grantedAuthoritiesAdmin =
            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN");
        controller.when(() -> AuthenticationController.getCustomerInfo(username)).thenReturn(null);
        controller.when(() -> AuthenticationController.getAdminInfo(username))
            .thenReturn(new AppUser(username, password, adminRole));
        assertEquals(userDetailsService.loadUserByUsername(username),
            new User(username, password, grantedAuthoritiesAdmin));
    }

    @Test
    void userNameNotFoundTest() {
        controller.when(() -> AuthenticationController.getCustomerInfo(username)).thenReturn(null);
        controller.when(() -> AuthenticationController.getAdminInfo(username)).thenReturn(null);
        assertThrows(UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(username));

    }

}
