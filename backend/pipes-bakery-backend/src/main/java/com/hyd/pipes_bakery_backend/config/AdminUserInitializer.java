package com.hyd.pipes_bakery_backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.model.Client;
import com.hyd.pipes_bakery_backend.model.Role;
import com.hyd.pipes_bakery_backend.repository.ClientRepository;

@Component
@Profile("!test")
public class AdminUserInitializer implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@melik.com";
    private static final String ADMIN_PASSWORD = "P1p3P4n4d3r0";

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        clientRepository.findByEmail(ADMIN_EMAIL)
                .ifPresentOrElse(this::ensureAdminPermissions, this::createAdminUser);
    }

    private void createAdminUser() {
        Client admin = new Client();
        admin.setFirstName("Admin");
        admin.setLastName("Melik");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(Role.ADMIN);

        clientRepository.save(admin);
    }

    private void ensureAdminPermissions(Client existingAdmin) {
        boolean shouldUpdate = false;

        if (existingAdmin.getRole() != Role.ADMIN) {
            existingAdmin.setRole(Role.ADMIN);
            shouldUpdate = true;
        }

        if (!passwordEncoder.matches(ADMIN_PASSWORD, existingAdmin.getPassword())) {
            existingAdmin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            shouldUpdate = true;
        }

        if (shouldUpdate) {
            clientRepository.save(existingAdmin);
        }
    }
}
