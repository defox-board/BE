package devfox.board.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ResponseBoardDetailDto {

    @Schema(description = "掲示板id")
    private Long id;
    @Schema(description = "掲示板題名")
    private String title;
    @Schema(description = "掲示板内容")
    private String content;
    @Schema(description = "作成者名前(id)")
    private String username;
    @Schema(description = "作成日時")
    private LocalDateTime createdAt;
    @Schema(description = "修正日時")
    private LocalDateTime updatedAt;


}
