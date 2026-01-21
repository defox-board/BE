package devfox.board.board.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentDto {

    @Schema(description = "コメントid")
    private Long id;
    @Schema(description = "コメント内容")
    private String content;
    @Schema(description = "作成者名前(ID)")
    private String username;
    @Schema(description = "作成日時")
    private LocalDateTime createdAt;
    @Schema(description = "修正日時")
    private LocalDateTime updatedAt;


}
