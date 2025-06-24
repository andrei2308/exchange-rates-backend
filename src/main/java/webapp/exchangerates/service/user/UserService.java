package webapp.exchangerates.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.exchangerates.domain.enums.UserRole;
import webapp.exchangerates.domain.model.User;
import webapp.exchangerates.exceptions.UserNotFoundException;
import webapp.exchangerates.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .active(true)
                .build();

        user.addRole(UserRole.USER);

        return userRepository.save(user);
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public Optional<User> findByWalletAddress(String walletAddress) {
        return userRepository.findByWalletAddress(walletAddress);
    }

    @Transactional
    public User updateWalletAddress(String userId, String walletAddress) {
        User user = findById(userId);
        user.setWalletAddress(walletAddress);
        return userRepository.save(user);
    }

    @Transactional
    public User updatePreferredCurrencies(String userId, String currencies) {
        User user = findById(userId);
        user.setPreferredCurrency(currencies);
        return userRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(String userId) {
        userRepository.updateLastLogin(userId, LocalDateTime.now());
    }

    @Transactional
    public void updateStripeCustomerId(String userId, String stripeCustomerId) {
        userRepository.updateStripeCustomerId(userId, stripeCustomerId);
    }
}