package com.example.sndi.service.jwt;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.sndi.model.User;
import com.example.sndi.repository.UserRepository;

@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("utilisateur introuvable" + username));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                Collections.emptyList());
    }
}
