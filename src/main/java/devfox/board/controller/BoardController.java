package devfox.board.controller;

import devfox.board.dto.request.CreateBoardDto;
import devfox.board.dto.response.ResponseBoardDetailDto;
import devfox.board.dto.response.ResponseBoardDto;
import devfox.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
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

    //掲示板の全体一覧取得APIにページング処理を実装
    @Operation(summary = "掲示板全体一覧取得")
    @GetMapping("/boards")
    public Page<ResponseBoardDto> getAllBoards(
            @PageableDefault(page = 0, size = 20, sort = "createdAt")
            Pageable pageable) {


        return boardService.getAllBoard(pageable);
    }
    //投稿を保存するAPIを実装
    @Operation(summary = "投稿を保存する")
    @PostMapping("/board")
    public ResponseEntity<?> saveBoard(Authentication authentication,
                                       @RequestBody CreateBoardDto dto) {

        boardService.save(dto, authentication.getName());
        return ResponseEntity.ok("作成完了");
    }
    //フロントエンドから受け取った boardId を基に、
    //該当する投稿の詳細情報を取得するAPI を実装
    @Operation(summary = "掲示板詳細情報一つ取得")
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<ResponseBoardDetailDto> getBoardById(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(boardService.getBoardById(boardId));
    }
    //フロントエンドから受け取った boardId を基に、
    //対象となる投稿を削除する 削除API を実装
    @Operation(summary = "投稿削除")
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<String> deleteById(@PathVariable("boardId") Long boardId,
                                             Authentication authentication) {
        boardService.deleteById(boardId, authentication.getName());

        return ResponseEntity.ok("削除完了");
    }
    //フロントエンドから DTO と boardId を受け取り、
    //指定された投稿内容を更新する 投稿修正API を実装
    @Operation(summary = "投稿修正")
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<String> updateById(@PathVariable("boardId") Long boardId,
                                             @RequestBody CreateBoardDto dto,
                                             Authentication authentication) {

        boardService.update(dto, authentication.getName());
        return ResponseEntity.ok("update完了");
    }

}
