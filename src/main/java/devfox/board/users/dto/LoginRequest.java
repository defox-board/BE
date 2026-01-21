package devfox.board.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Schema(example = "testuser")
    @NotBlank
    private String username;
    @Schema(example = "123456")
    @NotBlank
    private String password;
}
