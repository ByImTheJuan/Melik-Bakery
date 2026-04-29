package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.model.Client;
import com.hyd.pipes_bakery_backend.repository.ClientRepository;

@Service
public class ClientUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    public ClientUserDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with email " + email));

        return new User(
                client.getEmail(),
                client.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + client.getRole().name()))
        );
    }
}
