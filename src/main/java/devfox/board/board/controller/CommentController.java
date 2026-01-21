package devfox.board.board.controller;

import devfox.board.board.dto.request.CreateComment;
import devfox.board.board.dto.request.UpdateComment;
import devfox.board.board.dto.response.CursorResponse;
import devfox.board.board.dto.response.ResponseComment;
import devfox.board.board.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    @Operation(summary = "コメント保存する")
    @PostMapping
    public ResponseEntity<String> save(@RequestBody CreateComment dto,
                                       Authentication authentication) {

        commentService.save(authentication.getName(), dto);
        return ResponseEntity.ok("コメント登録完了");

    }
    @Operation(summary = "コメント取得(Cursor方式)")
    @GetMapping("/{boardId}")
    public ResponseEntity<CursorResponse<ResponseComment>> getCommentByBoardId
            (@PathVariable("boardId") Long boardId,
             @RequestParam(required = false) Long cursor,
             @RequestParam(defaultValue = "3") int limit) {

        CursorResponse result = commentService.findByBoardId(boardId, cursor, limit);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "コメント削除")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteById(Authentication authentication,
                                             @PathVariable("commentId") Long commentId

    ) {
        commentService.deleteById(authentication.getName(), commentId);
        return ResponseEntity.ok("削除完了");
    }

    @Operation(summary = "コメント修正")
    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(Authentication authentication,
                                                @RequestBody UpdateComment dto,
                                                @PathVariable("commentId") Long commentId) {

        commentService.updateComment(authentication.getName(),commentId,dto);
        return ResponseEntity.ok("修正完了");
    }

}
