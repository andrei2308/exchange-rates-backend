package webapp.exchangerates.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class LoginVM {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
