package com.test.demo.service.auth;

import com.test.demo.dto.UserDTO;
import com.test.demo.model.Role;
import com.test.demo.model.User;
import com.test.demo.repository.RoleRepository;
import com.test.demo.repository.UserRepository;
import com.test.demo.service.email.implementations.VerificationEmailService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationEmailService verificationEmail;
    private static final String SECRET_KEY = "123321123";


    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private void saveUserWithRole(User user, String roleName) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
        userRepository.save(user);
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
        saveUser(user);

        String verificationToken = generateVerificationToken(user);

        verificationEmail.setToken(verificationToken).setUser(user).sendEmail(user.getEmail());

    }

    public boolean verifyUser(String token) {
        String decodedToken = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        String[] parts = decodedToken.split("\\|");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Unprocessable token");
        }

        String email = parts[0];
        Optional<User> userOptional = userRepository.findByEmailAndEmailVerifiedFalse(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();

        if (!verifyToken(decodedToken)) {
            throw new IllegalArgumentException("Wrong token");
        }

        user.setEmailVerified(true);
        userRepository.save(user);
        return true;
    }

    private String generateVerificationToken(User user) {
        String data = user.getEmail() + "|" + java.time.LocalDateTime.now().plusDays(1);
        String hash = hashWithSHA256(data + SECRET_KEY);
        return Base64.getEncoder().encodeToString((data + "|" + hash).getBytes(StandardCharsets.UTF_8));
    }

    private boolean verifyToken(String decodedToken) {
        String[] parts = decodedToken.split("\\|");
        String email = parts[0];
        String expirationDate = parts[1];
        String tokenHash = parts[2];

        String originalData = email + "|" + expirationDate;
        String expectedHash = hashWithSHA256(originalData + SECRET_KEY);

        return expectedHash.equals(tokenHash) && java.time.LocalDateTime.now().isBefore(java.time.LocalDateTime.parse(expirationDate));
    }

    private String hashWithSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error creating SHA-256 hash", e);
        }
    }
}