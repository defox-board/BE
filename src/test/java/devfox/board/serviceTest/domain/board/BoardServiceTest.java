package devfox.board.serviceTest.domain.board;

import devfox.board.board.dto.request.CreateBoardDto;
import devfox.board.board.dto.response.ResponseBoardDetailDto;
import devfox.board.board.dto.response.ResponseBoardDto;
import devfox.board.board.entity.Board;
import devfox.board.board.repository.board.BoardRepositoryJDBC;
import devfox.board.users.entity.Users;
import devfox.board.board.repository.board.BoardRepositoryJpa;
import devfox.board.users.repository.UserRepository;
import devfox.board.users.repository.UserRepositoryJpa;
import devfox.board.board.service.BoardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {


    @Mock
    UserRepositoryJpa userRepositoryJpa;

    @Mock
    BoardRepositoryJpa boardRepositoryJpa;
    @InjectMocks
    BoardService boardService;

    @Mock
    BoardRepositoryJDBC boardRepositoryJDBC;

    @Mock
    UserRepository userRepository;


    @Test
    void 投稿作成_DB正常呼び出し() {


        //given
        CreateBoardDto boardDto = createBoardDto();
        String username = "username";

        Users users = Users.builder()
                .id(1L)
                .username(username)
                .build();


        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(users));
        //when
        boardService.save(boardDto, username);
        //then
        verify(boardRepositoryJpa, times(1)).save(any(Board.class));
    }

    @Test
    void 投稿作成_エンティティにDTOが正常に登録() {

        //given
        CreateBoardDto boardDto = createBoardDto();
        ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);
        String username = "username";


        Users users = Users.builder()
                .id(1L)
                .username(username)
                .build();


        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(users));

        //when
        boardService.save(boardDto, username);

        //then
        verify(boardRepositoryJpa, times(1)).save(boardCaptor.capture());
        Board value = boardCaptor.getValue();
        assertThat(value.getContent()).isEqualTo(boardDto.getContent());
        assertThat(value.getTitle()).isEqualTo(boardDto.getTitle());


    }

    @Test
    void 掲示板_全件_取得_DB呼び出し_正常() {

        //given

        Users user = Users.builder()
                .id(1L)

                .build();


        Board build = Board.builder()
                .title("title")
                .userId(1L)
                .content("content")
                .build();

        Board build2 = Board.builder()
                .title("title2")
                .userId(1L)
                .content("content2")
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        PageImpl<Board> boardPage = new PageImpl<>(List.of(build, build2), pageRequest, 2);
        given(boardRepositoryJpa.findAll(pageRequest))
                .willReturn(boardPage);

        given(userRepositoryJpa.findAllByUserIdOfBoard(any()))
                .willReturn(List.of(user));

        //when

        Page<ResponseBoardDto> result = boardService.getAllBoard(pageRequest);
        //then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ResponseBoardDto::getContent)
                .containsExactlyInAnyOrder("content", "content2");

        assertThat(result.getContent().get(0).getUserId())
                .isEqualTo(user.getId());
    }

    @Test
    void 掲示板_単件取得時_存在しない_掲示板ID_エラー発生() {

        //given

        Long wrongId = 1L;
        given(boardRepositoryJpa.findById(wrongId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.getBoardById(wrongId))
                .hasMessageContaining("投稿が見つかりません");

    }

    @Test
    void 掲示板_単件取得時_存在しない_作成者_エラー発生() {

        //given
        Long boardId = 1L;
        given(boardRepositoryJpa.findById(boardId))
                .willReturn(Optional.of(Board.
                        builder()
                        .userId(1L)
                        .build()));
        given(userRepositoryJpa.findById(1L))
                .willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> boardService.getBoardById(boardId))
                .hasMessageContaining("ユーザーを見つかりません");
    }


    @Test
    void 掲示板_1件取得時_値_正常返却() {

        //given
        Users users = Users.builder()
                .id(1L)
                .build();

        Board build = Board.builder()
                .id(1L)
                .userId(1L)
                .title("title")
                .content("content")
                .build();
        given(boardRepositoryJpa.findById(build.getId()))
                .willReturn(Optional.of(build));

        given(userRepositoryJpa.findById(build.getUserId()))
                .willReturn(Optional.of(users));
        //when

        ResponseBoardDetailDto result = boardService.getBoardById(1L);

        //then
        assertThat(result.getContent()).isEqualTo(build.getContent());
        assertThat(result.getTitle()).isEqualTo(build.getTitle());


    }


    @Test
    void 掲示板削除_DB＿正常＿呼び出し() {

        //given
        Board board = Board.builder()
                .id(1L)
                .userId(1L)
                .build();
        Users users = Users.builder()
                .username("username")
                .id(1L)
                .build();

        given(boardRepositoryJpa.findById(board.getId()))
                .willReturn(Optional.of(board));

        given(userRepositoryJpa.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));

        //when
        boardService.deleteById(board.getId(), users.getUsername());

        //then

        verify(boardRepositoryJpa, times(1)).delete(board);

    }

    @Test
    void 投稿削除＿作者じゃないなら＿エラー発生() {

        //given
        Board board = Board.builder()
                .id(1L)
                .userId(2L)
                .build();

        Users users = Users.builder()
                .username("username")
                .id(1L)
                .build();

        given(boardRepositoryJpa.findById(board.getId()))
                .willReturn(Optional.of(board));


        given(userRepositoryJpa.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));

        //when & then
        assertThatThrownBy(() -> boardService.deleteById(board.getId(), users.getUsername()))
                .hasMessageContaining("作者のみ削除可能です");

    }

    @Test
    void 投稿削除＿存在しない＿投稿なら＿エラー発生() {


        //given
        Long wrongId = 1L;
        String username = "username";
        given(boardRepositoryJpa.findById(wrongId))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() ->
                boardService.deleteById(wrongId, username)
        ).hasMessageContaining("存在しない掲示板");
    }


    @Test
    void 投稿修正＿値＿正常に変更() {

        //given
        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setId(1L);
        createBoardDto.setContent("changecontent");
        createBoardDto.setTitle("changetitle");

        Board board = Board.builder()
                .id(1L)
                .userId(1L)
                .title("title")
                .content("content")
                .build();

        given(boardRepositoryJpa.findById(createBoardDto.getId()))
                .willReturn(Optional.of(board));

        Users users = Users.builder()
                .id(1L)
                .username("username")
                .build();


        given(userRepositoryJpa.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));

        //when
        boardService.update(createBoardDto, users.getUsername());
        //then
        assertThat(board.getContent()).isEqualTo("changecontent");
        assertThat(board.getTitle()).isEqualTo("changetitle");

    }

    @Test
    void 投稿修正_作者じゃないなら＿エラー発生() {

        //given
        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setId(1L);
        createBoardDto.setContent("changecontent");
        createBoardDto.setTitle("changetitle");

        Board board = Board.builder()
                .id(1L)
                .userId(1L)
                .title("title")
                .content("content")
                .build();

        given(boardRepositoryJpa.findById(createBoardDto.getId()))
                .willReturn(Optional.of(board));

        Users users = Users.builder()
                .id(2L)
                .username("username")
                .build();


        given(userRepositoryJpa.findByUsername(users.getUsername()))
                .willReturn(Optional.of(users));

        //when & then

        assertThatThrownBy(() ->
                boardService.update(createBoardDto, users.getUsername()))
                .hasMessageContaining("作者のみ削除可能です");

    }

    @Test
    void 投稿修正_存在しない投稿なら＿エラー発生() {


        //given
        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setId(2L);
        createBoardDto.setContent("changecontent");
        createBoardDto.setTitle("changetitle");

        Board board = Board.builder()
                .id(1L)
                .userId(1L)
                .title("title")
                .content("content")
                .build();
        Users users = Users.builder()
                .id(2L)
                .username("username")
                .build();


        given(boardRepositoryJpa.findById(createBoardDto.getId()))
                .willReturn(Optional.empty());


        //when & then


        assertThatThrownBy(() -> boardService.update(createBoardDto, users.getUsername()))
                .hasMessageContaining("投稿が見つかりません");
    }




    @Test
    void 掲示板_title_検索して＿該当する_掲示板_取得() {

        //given

        Board board1 = Board.builder()
                .title("title")
                .build();

        Board board2 = Board.builder()
                .title("title2")
                .build();

        ResponseBoardDto dto1 = ResponseBoardDto.builder()
                .title("title")
                .build();


        ResponseBoardDto dto2 = ResponseBoardDto.builder()
                .title("title2")
                .build();

        PageRequest pageRequest = PageRequest.of(1, 2);


        given(boardRepositoryJDBC.findBySearchByLike(pageRequest, "title"))
                .willReturn(List.of(dto1, dto2));

        given(boardRepositoryJpa.count()).willReturn(2L);

        //when

        Page<ResponseBoardDto> result = boardService.findBySearchingByLike(pageRequest, "title");


        //then
        assertThat(result).hasSize(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("title");


    }




    @Test
    void 掲示板_title_検索して＿該当する_掲示板がない場合_Empty() {

        //given



        PageRequest pageRequest = PageRequest.of(1, 2);


        given(boardRepositoryJDBC.findBySearchByLike(pageRequest, "title"))
                .willReturn(List.of());

        given(boardRepositoryJpa.count()).willReturn(0L);

        //when

        Page<ResponseBoardDto> result = boardService.findBySearchingByLike(pageRequest, "title");


        //then
        assertThat(result).hasSize(0);


    }

    private static CreateBoardDto createBoardDto() {
        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setTitle("title");
        createBoardDto.setContent("content");
        return createBoardDto;
    }


}
