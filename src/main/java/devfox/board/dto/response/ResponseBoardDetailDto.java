package devfox.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ResponseBoardDetailDto {

    private Long id;
    private String title;
    private String content;
    private String username;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
