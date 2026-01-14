package devfox.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Schema(example = "dongik9467@naver.com")
    private String username;
    private String password;
}
