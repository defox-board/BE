package devfox.board.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoardDto {

    private String title;
    private String content;
}
