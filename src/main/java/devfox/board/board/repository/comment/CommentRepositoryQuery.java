package devfox.board.board.repository.comment;

import devfox.board.board.dto.response.CommentDto;

import java.util.List;

public interface CommentRepositoryQuery {


    List<CommentDto> findByBoardId(Long boardId, Long cursorId, int size);

}
