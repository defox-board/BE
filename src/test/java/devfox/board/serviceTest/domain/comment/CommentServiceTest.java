package devfox.board.serviceTest.domain.comment;

import devfox.board.board.dto.request.CreateComment;
import devfox.board.board.dto.response.CommentDto;
import devfox.board.board.dto.response.CursorResponse;
import devfox.board.board.entity.Board;
import devfox.board.users.entity.UserRole;
import devfox.board.users.entity.Users;
import devfox.board.board.repository.board.BoardRepositoryJpa;
import devfox.board.board.repository.comment.CommentRepository;
import devfox.board.users.repository.UserRepositoryJpa;
import devfox.board.board.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import devfox.board.board.entity.Comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void コメント登録_ユーザーなし_エラー() {

        //given
        CreateComment dto = new CreateComment();
        dto.setContent("content");
        dto.setBoardId(1L);
        given(userRepositoryJpa.findByUsername("username"))
                .willReturn(Optional.empty());


        //when & then
        assertThatThrownBy(() -> commentService.save("username", dto)
        ).hasMessageContaining("ユーザーを見つかりません");

    }
    @Test
    void コメント登録_掲示板なし_エラー() {

        //given
        CreateComment dto = new CreateComment();
        dto.setContent("content");
        dto.setBoardId(1L);
        given(userRepositoryJpa.findByUsername("username"))
                .willReturn(Optional.of(Users.builder().build()));
        given(boardRepositoryJpa.findById(dto.getBoardId()))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> commentService.save("username", dto)
        ).hasMessageContaining("掲示板を見つかりません");


    }

    @Test
    void コメント取得_hasNext_true() {

        //given

        Comment comment = Comment.builder()
                .id(1L)
                .build();
        given(boardRepositoryJpa.findById(any(Long.class)))
                .willReturn(Optional.of(Board.builder()
                        .build()));

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .build();

        CommentDto commentDto2 = CommentDto.builder()
                .id(2L)
                .build();

        CommentDto commentDto3 = CommentDto.builder()
                .id(3L)
                .build();

        CommentDto commentDto4 = CommentDto.builder()
                .id(4L)
                .build();

        given(commentRepository.findByBoardId(1L, null, 3))
                .willReturn(List.of(commentDto, commentDto2, commentDto3, commentDto4));


        //when

        CursorResponse result = commentService.findByBoardId(1L, null, 3);
        //then
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getNextCursor()).isEqualTo(4L);

    }


    @Test
    void コメント取得_hasNext_false() {

        //given

        Comment comment = Comment.builder()
                .id(1L)
                .build();
        given(boardRepositoryJpa.findById(any(Long.class)))
                .willReturn(Optional.of(Board.builder()
                        .build()));

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .build();

        CommentDto commentDto2 = CommentDto.builder()
                .id(2L)
                .build();


        given(commentRepository.findByBoardId(1L, null, 3))
                .willReturn(List.of(commentDto, commentDto2));

        //when

        CursorResponse result = commentService.findByBoardId(1L, null, 3);
        //then
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getNextCursor()).isNull();
    }

    @Test
    void コメント取得_掲示板なし_エラー() {

        //given
        given(boardRepositoryJpa.findById(any(Long.class)))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> commentService.findByBoardId(1L, null, 3))
                .hasMessageContaining("存在しない投稿");

    }

    @Test
    void 作成者ではない_場合_エラー発生() {

        //given

        Users users = Users.builder()
                .id(1L)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .userId(2L)
                .build();

        given(commentRepository.findById(1L))
                .willReturn(Optional.of(comment));

        given(userRepositoryJpa.findByUsername("username"))
                .willReturn(Optional.of(users));



        //when & then
        assertThatThrownBy(() ->

                commentService.deleteById("username", 1L)
        ).hasMessageContaining("作成者のみ削除可能");
    }

    @Test
    void DB_正常に呼び出し() {

        //given

        Users users = Users.builder()
                .id(1L)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .userId(1L)
                .build();

        given(commentRepository.findById(1L))
                .willReturn(Optional.of(comment));

        given(userRepositoryJpa.findByUsername("username"))
                .willReturn(Optional.of(users));

        //when
        commentService.deleteById("username", 1L);

        //then

        verify(commentRepository, times(1)).deleteById(1L);

    }


    @Test
    void コメント修正＿値＿正常に変更() {


        //given

        CreateComment dto = new CreateComment();
        dto.setBoardId(1L);
        dto.setContent("changeContent");

        Users users = Users.builder()
                .id(1L)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .userId(1L)
                .content("content")
                .build();

        given(userRepositoryJpa.findByUsername("username"))
                .willReturn(Optional.of(users));

        given(commentRepository.findById(1L))
                .willReturn(Optional.of(comment));


        //when
        commentService.updateComment("username", 1L, dto);

        //then
        assertThat(comment.getContent()).isEqualTo("changeContent");
    }

}
