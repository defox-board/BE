package devfox.board.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoardDto {
    @Schema(example = "1",description = "掲示板ID")
    private Long id;
    @Schema(example = "title",description = "掲示板題名")
    private String title;
    @Schema(example = "content",description = "掲示板内容")
    private String content;
}
