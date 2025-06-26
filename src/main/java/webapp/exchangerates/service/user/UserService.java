package webapp.exchangerates.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import webapp.exchangerates.configuration.security.JwtUtil;
import webapp.exchangerates.domain.dto.LoginVM;
import webapp.exchangerates.domain.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public UserDto login(LoginVM loginVM) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginVM.getUsername());

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(loginVM.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UserDto user = new UserDto();
        String token = jwtUtil.createTokenString(userDetails);
        long expirationTime = 86_400_000;

        user.setToken(token);
        user.setExpiresIn(expirationTime);

        return user;
    }
}