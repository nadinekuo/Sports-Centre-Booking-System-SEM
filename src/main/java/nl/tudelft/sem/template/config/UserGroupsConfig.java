package nl.tudelft.sem.template.config;

import java.beans.Customizer;
import java.util.List;
import nl.tudelft.sem.template.entities.Admin;
import nl.tudelft.sem.template.entities.Customer;
import nl.tudelft.sem.template.entities.Group;
import nl.tudelft.sem.template.repositories.GroupRepository;
import nl.tudelft.sem.template.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserGroupsConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(UserRepository<Customer> customerRepository,
                                            UserRepository<Admin> adminRepository,
                                            GroupRepository groupRepository) {

        return args -> {
            Customer arslan = new Customer("arslan123", "password1", true);
            Customer emil = new Customer("emil123", "password2", false);
            Customer emma = new Customer("emma123", "password3", true);
            Customer erwin = new Customer("erwin123", "password4", false);
            Customer nadine = new Customer("nadine123", "password5", true);
            Customer panagiotis = new Customer("panas123", "password6", false);

            Admin admin1 = new Admin("admin1", "randomstring1");

            customerRepository.saveAll(List.of(arslan, emil, emma, erwin, nadine, panagiotis));
            adminRepository.saveAll(List.of(admin1));

            Group g1 = new Group("soccerTeam1", List.of(arslan, emil));
            Group g2 = new Group("volleyballTeam3", List.of(emma, erwin));

            groupRepository.saveAll(List.of(g1, g2));

        };
    }

}
