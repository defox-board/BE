package devfox.security.dto;

public record JWTResponseDTO(
        String accessToken,
        String refreshToken
) {
}
