package devfox.board.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateComment {

    @Schema(example = "update", description = "コメント修正")
    private String content;

}
