package webapp.exchangerates.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private long expiresIn;
    private String token;
}
