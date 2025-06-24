package webapp.exchangerates.domain.dto;

import lombok.Builder;
import lombok.Data;
import webapp.exchangerates.domain.enums.KycStatus;
import webapp.exchangerates.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private String id;
    private String email;
    private String name;
    private String walletAddress;
    private String preferredCurrency;
    private boolean active;
    private boolean verified;
    private KycStatus kycStatus;
    private Set<UserRole> roles;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
