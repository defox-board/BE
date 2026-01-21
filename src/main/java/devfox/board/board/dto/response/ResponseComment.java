package devfox.board.board.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class ResponseComment {

        @Schema(description = "コメントid")
        private Long commentId;
        @Schema(description = "作成者名前(id)")
        private String username;
        @Schema(description = "コメント内容")
        private String comment;
    @Schema(description = "作成日時")
    private LocalDateTime createdAt;
    @Schema(description = "修正日時")
    private LocalDateTime updatedAt;
}
