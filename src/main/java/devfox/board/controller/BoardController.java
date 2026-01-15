package devfox.board.controller;

import devfox.board.dto.request.CreateBoardDto;
import devfox.board.dto.response.ResponseBoardDetailDto;
import devfox.board.dto.response.ResponseBoardDto;
import devfox.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;


    @GetMapping("/boards")
    public Page<ResponseBoardDto> getAllBoards(
            @PageableDefault(page = 0, size = 20, sort = "createdAt")
            Pageable pageable) {


        return boardService.getAllBoard(pageable);
    }

    @PostMapping("/board")
    public ResponseEntity<?> saveBoard(Authentication authentication,
                                       @RequestBody CreateBoardDto dto) {

        boardService.save(dto, authentication.getName());
        return ResponseEntity.ok("作成完了");
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<ResponseBoardDetailDto> getBoardById(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(boardService.getBoardById(boardId));
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<String> deleteById(@PathVariable("boardId") Long boardId,
                                             Authentication authentication) {
        boardService.deleteById(boardId, authentication.getName());

        return ResponseEntity.ok("削除完了");
    }

    @PutMapping("/boards/{boardId}")
    public ResponseEntity<String> updateById(@PathVariable("boardId") Long boardId,
                                             @RequestBody CreateBoardDto dto,
                                             Authentication authentication) {

        boardService.update(dto, authentication.getName());
        return ResponseEntity.ok("update完了");
    }

}
