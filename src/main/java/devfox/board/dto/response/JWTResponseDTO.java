package devfox.board.dto.response;

public record JWTResponseDTO(
        String accessToken,
        String refreshToken
) {
}
