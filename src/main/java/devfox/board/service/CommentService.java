package devfox.board.service;

import devfox.board.dto.request.CreateComment;
import devfox.board.dto.response.CommentDto;
import devfox.board.dto.response.CursorResponse;
import devfox.board.entity.Comment;
import devfox.users.entity.Users;
import devfox.board.repository.board.BoardRepositoryJpa;
import devfox.board.repository.comment.CommentRepository;
import devfox.users.repository.UserRepositoryJpa;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {


    private final CommentRepository commentRepository;
    private final UserRepositoryJpa userRepositoryJpa;
    private final BoardRepositoryJpa boardRepositoryJpa;



    @Transactional
    public void save(String username, CreateComment dto) {


        Long userId = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーを見つかりません"))
                .getId();


        Long boardId = boardRepositoryJpa.findById(dto.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("掲示板を見つかりません"))
                .getId();


        Comment comment = Comment.builder()
                .boardId(boardId)
                .content(dto.getContent())
                .userId(userId)
                .build();

        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public CursorResponse findByBoardId(Long boardId, Long cursorId, int size) {

        boardRepositoryJpa.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("存在しない投稿"));

        List<CommentDto> commentList = commentRepository.findByBoardId(boardId, cursorId, size);

        boolean hasNext = commentList.size() > size;

        Long nextCursor = hasNext
                ? commentList.get(commentList.size() - 1).getId()
                : null;
        return CursorResponse.builder()
                .data(commentList)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    @Transactional
    @Modifying
    public void deleteById(String username, Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("コメントを見つかりません"));

        Users user = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("ユーザーを見つかりません"));

        if (comment.getUserId() != user.getId()) {
            throw new IllegalArgumentException("作成者のみ削除可能");
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void updateComment(String username, Long commentId, CreateComment dto) {


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("コメントを見つかりません"));

        Users user = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("ユーザーを見つかりません"));

        if (comment.getUserId() != user.getId()) {
            throw new IllegalArgumentException("作成者のみ修正可能");
        }

        comment.update(dto);
    }


}
