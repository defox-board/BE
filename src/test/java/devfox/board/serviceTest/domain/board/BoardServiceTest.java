package devfox.board.serviceTest.domain.board;

import devfox.board.dto.request.CreateBoardDto;
import devfox.board.entity.Board;
import devfox.board.repository.BoardRepositoryJpa;
import devfox.board.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {


    @Mock
    BoardRepositoryJpa boardRepositoryJpa;
    @InjectMocks
    BoardService boardService;



    @Test
    void 投稿作成_DB正常呼び出し() {


        //given
        CreateBoardDto boardDto = createBoardDto();
        String username = "username";
        //when
        boardService.save(boardDto,username);
        //then
        verify(boardRepositoryJpa, times(1)).save(any(Board.class));
        }

        @Test
        void 投稿作成_エンティティにDTOが正常に登録() {

        //given
            CreateBoardDto boardDto = createBoardDto();
            ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);
            String username = "username";

            //when
        boardService.save(boardDto,username);

        //then
            verify(boardRepositoryJpa, times(1)).save(boardCaptor.capture());
            Board value = boardCaptor.getValue();
            assertThat(value.getContent()).isEqualTo(boardDto.getContent());
            assertThat(value.getTitle()).isEqualTo(boardDto.getTitle());


        }



    private static CreateBoardDto createBoardDto() {
        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setTitle("title");
        createBoardDto.setContent("content");
        return createBoardDto;
    }

}
