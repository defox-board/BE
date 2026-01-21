package devfox.board.security.dto;

public record JWTResponseDTO(
        String accessToken,
        String refreshToken
) {
}
