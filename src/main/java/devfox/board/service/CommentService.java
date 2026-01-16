package devfox.board.service;

import devfox.board.dto.request.CreateComment;
import devfox.board.dto.response.ResponseComment;
import devfox.board.entity.Board;
import devfox.board.entity.Comment;
import devfox.board.entity.Users;
import devfox.board.repository.board.BoardRepositoryJpa;
import devfox.board.repository.comment.CommentRepositoryJpa;
import devfox.board.repository.users.UserRepositoryJpa;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepositoryJpa commentRepositoryJpa;
    private final BoardRepositoryJpa boardRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;

    @Transactional
    public void save(CreateComment dto, String username) {

        Board board = boardRepositoryJpa.findById(dto.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("投稿が見つかりません"));

        Users users = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("ユーザーを見つかりません"));

        Comment comment = Comment.builder()
                .userId(users.getId())
                .boardId(board.getId())
                .content(dto.getContent())
                .build();
        commentRepositoryJpa.save(comment);

    }

    @Transactional
    //쿼리 줄일 수 없을까?
    public void update(Long commentId, CreateComment dto, String username) {

        Board board = boardRepositoryJpa.findById(dto.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("投稿が見つかりません"));

        Users users = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("ユーザーを見つかりません"));

        Comment comment = commentRepositoryJpa.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("コメント見つかりません"));

        comment.update(dto);
    }

    @Transactional
    public void delete(Long commentId, String username) {


        Users users = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("ユーザーを見つかりません"));

        Comment comment = commentRepositoryJpa.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("コメント見つかりません"));

        if (comment.getUserId() != users.getId()) {
            throw new IllegalArgumentException("ユーザーのみ削除可能");
        }
        commentRepositoryJpa.delete(comment);
    }

    @Transactional(readOnly = true)
    public Page<ResponseComment> getAllCommentByBoardId(Pageable pageable,Long boardId) {

        // 작업중
        return null;
    }
}
