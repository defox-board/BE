package devfox.board.repository.comment;

import devfox.board.dto.response.CommentDto;
import devfox.board.entity.Comment;

import java.util.List;

public interface CommentRepositoryQuery {


    List<CommentDto> findByBoardId(Long boardId, Long cursorId, int size);

}
