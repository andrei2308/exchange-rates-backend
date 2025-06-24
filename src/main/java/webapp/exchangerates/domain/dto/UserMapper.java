package webapp.exchangerates.domain.dto;

import org.springframework.stereotype.Component;
import webapp.exchangerates.domain.model.User;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .walletAddress(user.getWalletAddress())
                .preferredCurrency(user.getPreferredCurrency())
                .active(user.isActive())
                .verified(user.isVerified())
                .kycStatus(user.getKycStatus())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
