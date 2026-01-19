package devfox.board.controller;

import devfox.board.dto.request.CreateComment;
import devfox.board.dto.response.CursorResponse;
import devfox.board.dto.response.ResponseComment;
import devfox.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<String> save(@RequestBody CreateComment dto,
                                       Authentication authentication) {

        commentService.save(authentication.getName(), dto);
        return ResponseEntity.ok("コメント登録完了");

    }

    @GetMapping("/{boardId}")
    public ResponseEntity<CursorResponse<ResponseComment>> getCommentByBoardId
            (@PathVariable("boardId") Long boardId,
             @RequestParam(required = false) Long cursor,
             @RequestParam(defaultValue = "3") int limit) {

        CursorResponse result = commentService.findByBoardId(boardId,cursor,limit);
        return ResponseEntity.ok(result);
    }

}
