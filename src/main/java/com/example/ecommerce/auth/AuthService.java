package com.example.ecommerce.auth;

import static com.example.ecommerce.auth.AuthDtos.AuthResponse;
import static com.example.ecommerce.auth.AuthDtos.LoginRequest;
import static com.example.ecommerce.auth.AuthDtos.RegisterRequest;
import static com.example.ecommerce.auth.AuthDtos.UserSummary;

import com.example.ecommerce.security.AppUserPrincipal;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.user.Role;
import com.example.ecommerce.user.User;
import com.example.ecommerce.user.UserRepository;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user = new User(
                request.fullName().trim(),
                email,
                passwordEncoder.encode(request.password()),
                Set.of(Role.USER)
        );
        User saved = userRepository.save(user);
        var principal = new AppUserPrincipal(saved);
        return new AuthResponse(jwtService.generateToken(principal), toSummary(saved));
    }

    public AuthResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password())
        );
        var principal = (AppUserPrincipal) authentication.getPrincipal();
        return new AuthResponse(jwtService.generateToken(principal), toSummary(principal.getUser()));
    }

    private UserSummary toSummary(User user) {
        return new UserSummary(user.getId(), user.getFullName(), user.getEmail(), user.getRoles());
    }
}
