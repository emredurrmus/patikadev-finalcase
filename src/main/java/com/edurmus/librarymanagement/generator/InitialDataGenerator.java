package com.edurmus.librarymanagement.generator;

import com.edurmus.librarymanagement.model.entity.Role;
import com.edurmus.librarymanagement.model.entity.User;
import com.edurmus.librarymanagement.model.enums.UserRole;
import com.edurmus.librarymanagement.repository.RoleRepository;
import com.edurmus.librarymanagement.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitialDataGenerator {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String DEFAULT_CREATED_BY = "SYSTEM";

    @PostConstruct
    public void init() {

        if (roleRepository.count() == 0) {
            createRole(UserRole.ROLE_LIBRARIAN);
            log.info("Librarian role created");

            createRole(UserRole.ROLE_PATRON);
            log.info("Patron role created");
        }
        if (userRepository.count() == 0) {
            createLibrarianUser();
            log.info("Librarian user created");

        }
    }

    private void createLibrarianUser() {
        Set<Role> librarianRole = roleRepository.findByUserRole(UserRole.ROLE_LIBRARIAN);
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("pw135!."))
                .email("user@gmail.com")
                .phoneNumber("05356543256")
                .enabled(true)
                .roles(librarianRole)
                .build();
        user.setCreatedBy(DEFAULT_CREATED_BY);
        userRepository.save(user);
    }

    private void createRole(UserRole userRole) {
        Role role = Role.builder()
                .userRole(userRole)
                .build();
        role.setCreatedBy(DEFAULT_CREATED_BY);
        roleRepository.save(role);
    }
}
