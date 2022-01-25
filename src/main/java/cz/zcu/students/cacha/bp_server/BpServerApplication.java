package cz.zcu.students.cacha.bp_server;

import cz.zcu.students.cacha.bp_server.domain.Language;
import cz.zcu.students.cacha.bp_server.domain.Role;
import cz.zcu.students.cacha.bp_server.repositories.LanguageRepository;
import cz.zcu.students.cacha.bp_server.repositories.RoleRepository;
import cz.zcu.students.cacha.bp_server.security.JwtAuthenticationFilter;
import org.apache.tika.Tika;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static cz.zcu.students.cacha.bp_server.shared.RolesConstants.*;

@SpringBootApplication
public class BpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpServerApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean
    CommandLineRunner run(RoleRepository roleRepository, LanguageRepository languageRepository) {
        return args -> {
            if(roleRepository.count() == 0) {
                Role author = new Role(ROLE_TRANSLATOR);
                roleRepository.save(author);

                Role reviewer = new Role(ROLE_INSTITUTION_OWNER);
                roleRepository.save(reviewer);

                Role admin = new Role(ROLE_ADMIN);
                roleRepository.save(admin);
            }

            if(languageRepository.count() == 0) {
                try(InputStream is =  getClass().getClassLoader().getResourceAsStream("static/language_codes.csv")) {
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        while(reader.ready()) {
                            String line = reader.readLine();
                            String[] languageValues = line.split(",");

                            Language language = new Language(languageValues[1], languageValues[0]);
                            languageRepository.save(language);
                        }
                    }
                }
            }
        };
    }
}
