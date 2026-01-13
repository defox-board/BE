package devfox.board.jwt;

import devfox.board.dto.request.RefreshRequestDto;
import devfox.board.dto.response.JWTResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public void addRefresh(String username, String refreshToken) {
        RefreshEntity entity =
                RefreshEntity.builder()
                        .username(username)
                        .refresh(refreshToken)
                        .build();
        int result = refreshRepository.save(entity);
        if (result == 0) {
            throw new RuntimeException("サーバーエラーが発生しました。");
        }
    }
    @Transactional(readOnly = true)
    public Boolean existsByRefresh(String refreshToken) {
        int result = refreshRepository.existsByRefresh(refreshToken);
        if (result == 1) {
            return true;
        } else return false;
    }

    @Transactional
    public void deleteRefresh(String refreshToken) {
        int result = refreshRepository.delete(refreshToken);
        if (result == 0) {
            throw new RuntimeException("サーバーエラーが発生しました。");
        }
    }

    @Transactional
    public void deleteRefreshByUsername(String username) {

        int result = refreshRepository.deleteByUsername(username);
        if (result == 0) {
            throw new RuntimeException("サーバーエラーが発生しました。");
        }

    }

    @Transactional
    public JWTResponseDTO refreshRotate(RefreshRequestDto dto) {

        String refreshToken = dto.getRefreshToken();

        // Refresh 토큰 검증
        Boolean isValid = jwtUtil.isValid(refreshToken, false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // RefreshEntity 존재 확인 (화이트리스트)
        if (!existsByRefresh(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(username, role, true);
        String newRefreshToken = jwtUtil.createJWT(username, role, false);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        RefreshEntity newRefreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build();

        deleteRefresh(refreshToken);
        refreshRepository.save(newRefreshEntity);

        return new JWTResponseDTO(newAccessToken, newRefreshToken);
    }




}
