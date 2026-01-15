package devfox.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ResponseBoardDto {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String username;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
