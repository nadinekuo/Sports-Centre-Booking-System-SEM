package nl.tudelft.sem.gateway.config;

import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.gateway.filters.JwtTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * The security configuration, handles authorization.
     *
     * @param http http
     * @throws Exception exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            // make sure we use stateless session; session won't be used to store user's state.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            // handle an authorized attempts
            .exceptionHandling().authenticationEntryPoint(
                (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)).and()
            // Add a filter to validate the tokens with every request
            .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig),
                UsernamePasswordAuthenticationFilter.class)
            // authorization requests config
            .authorizeRequests()
            // allow all who are accessing "eureka-security" service
            .antMatchers(jwtConfig.getUri()).permitAll()
            // whitelist register customer method, you don't need to be verified to register
            .antMatchers("/eureka-user/user/registerCustomer").permitAll()
            //must be an admin to access these methods
            .antMatchers("/eureka-reservation/**" + "/admin/**").hasRole("ADMIN")
            .antMatchers("/eureka-sport-facilities/**" + "/admin/**").hasRole("ADMIN")
            .antMatchers("/eureka-user/**" + "/admin/**").hasRole("ADMIN")
            // Any other request must be authenticated
            .anyRequest().authenticated();
    }

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }
}
