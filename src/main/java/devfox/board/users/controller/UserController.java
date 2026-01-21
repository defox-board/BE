package devfox.board.users.controller;

import devfox.board.users.dto.LoginRequest;
import devfox.board.users.dto.UserRequestDto;
import devfox.board.users.dto.UserResponseDto;
import devfox.board.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //ユーザー存在チェック
    @Operation(summary = "ユーザー存在チェック")
    @PostMapping("/user/exist")
    public ResponseEntity<Boolean> existUser(
            @Validated(UserRequestDto.existGroup.class)
            @RequestBody UserRequestDto dto
    ) {
        return ResponseEntity.ok(userService.existUser(dto));
    }

    //会員登録（かいいんとうろく）
    @Operation(summary = "会員登録（かいいんとうろく）")
    @PostMapping("/join")
    public ResponseEntity<Map<String, Long>> join(
            @Validated(UserRequestDto.addGroup.class) @RequestBody UserRequestDto dto) {

        Long id = userService.addUser(dto);
        Map<String, Long> responseBody = Collections.singletonMap("userId", id);
        return ResponseEntity.ok(responseBody);
    }

    //ユーザー情報更新
    @Operation(summary = "ユーザー情報更新")
    @PutMapping("/user")
    public ResponseEntity<String> updateUser(Authentication authentication,
                                             @Validated(UserRequestDto.updateGroup.class)
                                             @RequestBody UserRequestDto dto) throws AccessDeniedException {

        userService.updateUser(dto, authentication.getName());
        return ResponseEntity.ok("update完了");
    }

    //ユーザー情報取得
    @Operation(summary = "ユーザー情報取得")
    @GetMapping("/user")
    public ResponseEntity<UserResponseDto> getUserInfo(Authentication authentication) {
        UserResponseDto result = userService.getUserInfo(authentication.getName());
        return ResponseEntity.ok(result);

    }

    //会員退会
    @Operation(summary = "会員退会")
    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(Authentication authentication,
                                             @Validated(UserRequestDto.deleteGroup.class)
                                             @RequestBody UserRequestDto dto) throws AccessDeniedException {

        userService.deleteUser(dto, authentication.getName());
        return ResponseEntity.ok("ユーザー削除完了");

    }

    @Operation(summary = "ログイン")
    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest) {

    }
}
