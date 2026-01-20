package devfox.board.integrationTest.domain.jwt;

import devfox.security.jwt.JWTUtil;
import devfox.security.jwt.JwtService;
import devfox.security.jwt.RefreshEntity;
import devfox.security.jwt.RefreshRepository;
import devfox.board.repository.comment.CommentRepositoryQueryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class JwtTest {

    @Autowired
    JwtService jwtService;
    @Autowired
    RefreshRepository refreshRepository;
    @MockitoBean
    JWTUtil jwtUtil;
    @MockitoBean
    CommentRepositoryQueryImpl commentRepository;

    @BeforeEach
    void setUp() {

        //given
        String username = "username";
        String refresh = "refresh";

        //when
        jwtService.addRefresh(username, refresh);

    }

    @Test
    void Refresh_Token_저장_값확인() {

        //given
        String username = "username2";
        String refresh = "refresh2";

        //when
        jwtService.addRefresh(username, refresh);
        RefreshEntity refreshEntity =
                refreshRepository.findByUsername(username).get();

        //then
        assertThat(refreshEntity.getRefresh()).isEqualTo(refresh);
        assertThat(refreshEntity.getUsername()).isEqualTo(username);
    }

    @Test
    void Refresh_Token_이미_존재하면_에러() {

        //given
        String username = "username";
        String refresh = "refresh";

        //when & then
        assertThatThrownBy(() -> jwtService.addRefresh(username, refresh))
                .isInstanceOf(DuplicateKeyException.class);

    }

    @Test
    void 정상_삭제_확인() {

        //given < BeforeEach setup

        //when
        jwtService.deleteRefresh("refresh");
        Optional<RefreshEntity> result = refreshRepository.findByUsername("username");

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void 유저네임기반_삭제_확인() {

        //given < BeforeEach setup

        //when
        jwtService.deleteRefreshByUsername("username");
        Optional<RefreshEntity> result = refreshRepository.findByUsername("username");
        //then
        assertThat(result).isEmpty();



    }
}
