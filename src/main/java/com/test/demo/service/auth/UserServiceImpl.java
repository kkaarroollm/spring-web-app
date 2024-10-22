package com.test.demo.service.auth;

import com.test.demo.dto.UserDTO;
import com.test.demo.model.Role;
import com.test.demo.model.User;
import com.test.demo.repository.RoleRepository;
import com.test.demo.repository.UserRepository;
import com.test.demo.service.email.implementations.VerificationEmail;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationEmail verificationEmail;

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveAdmin(User user) {
        saveUserWithRole(user, "ADMIN");
    }

    @Override
    public void saveUser(User user) {
        saveUserWithRole(user, "USER");
    }

    @Override
    @Transactional
    public void registerUser(UserDTO userDTO) throws MessagingException {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setPassword(userDTO.getPassword());
        user.setPhone(userDTO.getPhone());
        saveUserWithRole(user, "USER");
        verificationEmail.sendEmail(user.getEmail());
        /* TODO add sending token and add endpoint for verification and endpoint for sending activation email again */
    }

    private void saveUserWithRole(User user, String roleName) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
        userRepository.save(user);
    }
}