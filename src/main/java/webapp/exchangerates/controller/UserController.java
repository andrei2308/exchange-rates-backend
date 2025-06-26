package webapp.exchangerates.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import webapp.exchangerates.domain.dto.LoginVM;
import webapp.exchangerates.domain.dto.UserDto;
import webapp.exchangerates.service.user.UserService;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginVM loginVM) {
        try {
            return ResponseEntity.ok(userService.login(loginVM));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
