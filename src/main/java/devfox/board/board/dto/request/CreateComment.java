package devfox.board.board.dto.request;

import devfox.board.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateComment extends BaseEntity {
    //掲示板id
    @Schema(example = "1",description = "掲示板id" )
    private Long boardId;
    //コメント内容
    @Schema(example = "comment",description ="コメント内容" )
    private String content;
}
