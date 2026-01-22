package devfox.board.board.controller;

import devfox.board.board.dto.request.CreateBoardDto;
import devfox.board.board.dto.response.ResponseBoardDetailDto;
import devfox.board.board.dto.response.ResponseBoardDto;
import devfox.board.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    //掲示板の全体一覧取得APIにページング処理を実装
    @Operation(summary = "掲示板全体一覧取得", description = "掲示板の全体一覧取得APIにページング処理を実装")
    @GetMapping
    public ResponseEntity<Page<ResponseBoardDto>> getAllBoards(
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = DESC)
            Pageable pageable) {

        log.info("게시판 페이징 호출");

        return ResponseEntity.ok(boardService.getAllBoard(pageable));
    }

    @Operation(summary = "掲示板全体一覧取得 BY JDBC")
    @GetMapping("/byJDBC")
    public ResponseEntity<Page<ResponseBoardDto>> getAllBoardsByJDBC(
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = DESC)
                                                     Pageable pageable
    ) {
        return ResponseEntity.ok(boardService.getAllBoardBySql(pageable));

    }



    //投稿を保存するAPIを実装
    @Operation(summary = "投稿を保存する", description = "投稿を保存するAPIを実装")
    @PostMapping
    public ResponseEntity<?> saveBoard(Authentication authentication,
                                       @RequestBody CreateBoardDto dto) {

        boardService.save(dto, authentication.getName());
        return ResponseEntity.ok("作成完了");
    }
    //フロントエンドから受け取った boardId を基に、
    //該当する投稿の詳細情報を取得するAPI を実装
    @Operation(summary = "掲示板詳細情報一つ取得",description = """
             フロントエンドから受け取った boardId を基に、
                該当する投稿の詳細情報を取得するAPI を実装
            
            """)
    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseBoardDetailDto> getBoardById(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(boardService.getBoardById(boardId));
    }
    //フロントエンドから受け取った boardId を基に、
    //対象となる投稿を削除する 削除API を実装
    @Operation(summary = "投稿削除",description = """
            フロントエンドから受け取った boardId を基に、
                対象となる投稿を削除する 削除API を実装
            
            """)
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteById(@PathVariable("boardId") Long boardId,
                                             Authentication authentication) {
        boardService.deleteById(boardId, authentication.getName());

        return ResponseEntity.ok("削除完了");
    }
    //フロントエンドから DTO と boardId を受け取り、
    //指定された投稿内容を更新する 投稿修正API を実装
    @Operation(summary = "投稿修正",description = """
            フロントエンドから DTO と boardId を受け取り、
             指定された投稿内容を更新する 投稿修正API を実装
            """)
    @PutMapping("/{boardId}")
    public ResponseEntity<String> updateById(@PathVariable("boardId") Long boardId,
                                             @RequestBody CreateBoardDto dto,
                                             Authentication authentication) {

        boardService.update(dto, authentication.getName());
        return ResponseEntity.ok("update完了");
    }

    @GetMapping("/findByUser")
    public ResponseEntity<Page<ResponseBoardDto>> findByUser(Authentication authentication,
                                                             @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = DESC)
                                                             Pageable pageable) {


        Page<ResponseBoardDto> result = boardService.findByUser(pageable, authentication.getName());
        return ResponseEntity.ok(result);
    }

}
