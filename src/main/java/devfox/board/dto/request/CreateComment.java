package devfox.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateComment {
    @Schema(example = "1")
    private Long boardId;
    @Schema(example = "comment")
    private String content;
}
