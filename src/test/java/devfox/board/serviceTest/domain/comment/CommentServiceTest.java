package devfox.board.serviceTest.domain.comment;

import devfox.board.dto.request.CreateComment;
import devfox.board.entity.Board;
import devfox.board.entity.UserRole;
import devfox.board.entity.Users;
import devfox.board.repository.board.BoardRepository;
import devfox.board.repository.board.BoardRepositoryJpa;
import devfox.board.repository.comment.CommentRepository;
import devfox.board.repository.users.UserRepository;
import devfox.board.repository.users.UserRepositoryJpa;
import devfox.board.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import devfox.board.entity.Comment;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {


    @InjectMocks
    CommentService commentService;
    @Mock
    BoardRepositoryJpa boardRepositoryJpa;
    @Mock
    UserRepositoryJpa userRepositoryJpa;

    @Mock
    CommentRepository commentRepository;
    Users users;
    Board board;

    @BeforeEach
    void setUp() {

         users = Users.builder()
                .id(1L)
                .userRole(UserRole.USER)
                .username("username")
                .build();

        board = Board.builder()
                .id(1L)
                .userId(users.getId())
                .build();

    }


    @Test
    void コメント登録_正常() {
        //given

        CreateComment dto = new CreateComment();
        dto.setContent("content");
        dto.setBoardId(1L);
        given(userRepositoryJpa.findByUsername("username"))
                .willReturn(Optional.of(users));
        given(boardRepositoryJpa.findById(1L))
                .willReturn(Optional.of(board));


        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);


        //when
        commentService.save(users.getUsername(), dto);

        //then
        verify(commentRepository, times(1)).save(captor.capture());
        Comment value = captor.getValue();
        assertThat(value.getContent()).isEqualTo("content");

    }
}
