package devfox.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateComment {

    private Long boardId;
    private String content;
}
