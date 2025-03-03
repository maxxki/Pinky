package com.maxxki.task_manager_backend.security;

import com.maxxki.task_manager_backend.model.PinkyUser; // Änderung 1: Import zu PinkyUser geändert
import com.maxxki.task_manager_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PinkyUser user = userRepository.findByUsername(username) // Änderung 2: Typ der Variable zu PinkyUser geändert
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user; // Gibt jetzt PinkyUser zurück, was auch UserDetails ist
    }
}