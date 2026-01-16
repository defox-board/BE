package devfox.board.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoardDto {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "title")
    private String title;
    @Schema(example = "content")
    private String content;
}
