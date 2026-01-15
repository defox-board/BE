package devfox.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoardDto {

    private Long id;
    private String title;
    private String content;
}
