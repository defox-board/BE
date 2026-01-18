package devfox.board.service;

import devfox.board.dto.request.CreateComment;
import devfox.board.dto.response.CommentDto;
import devfox.board.dto.response.CursorResponse;
import devfox.board.entity.Comment;
import devfox.board.entity.Users;
import devfox.board.repository.board.BoardRepositoryJpa;
import devfox.board.repository.comment.CommentRepository;
import devfox.board.repository.users.UserRepositoryJpa;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {


    private final CommentRepository commentRepository;
    private final UserRepositoryJpa userRepositoryJpa;
    private final BoardRepositoryJpa boardRepositoryJpa;



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




}
