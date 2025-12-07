package com.englishplatform.service;

import com.englishplatform.model.User;
import com.englishplatform.model.UserProgress;
import com.englishplatform.repository.UserRepository;
import com.englishplatform.repository.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        // Create initial progress for new user
        UserProgress progress = new UserProgress(savedUser);
        userProgressRepository.save(progress);
        
        return savedUser;
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        if (existsByUsername(username)) {
            throw new RuntimeException("Username already exists!");
        }
        if (existsByEmail(email)) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User(username, email, password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        return saveUser(user);
    }
}