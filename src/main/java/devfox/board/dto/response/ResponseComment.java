package devfox.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class ResponseComment {


        private Long commentId;
        private String username;
        private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
