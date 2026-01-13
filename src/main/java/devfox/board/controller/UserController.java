package devfox.board.controller;

import devfox.board.dto.request.UserRequestDto;
import devfox.board.dto.response.UserResponseDto;
import devfox.board.service.UserService;
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

    @PostMapping("/user/exist")
    public ResponseEntity<Boolean> existUser(
            @Validated(UserRequestDto.existGroup.class)
            @RequestBody UserRequestDto dto
    ) {
        return ResponseEntity.ok(userService.existUser(dto));
    }


    @PostMapping("/join")
    public ResponseEntity<Map<String, Long>> join(
            @Validated(UserRequestDto.addGroup.class) @RequestBody UserRequestDto dto) {

        Long id = userService.addUser(dto);
        Map<String, Long> responseBody = Collections.singletonMap("userId", id);
        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/user")
    public ResponseEntity<String> updateUser(Authentication authentication,
                                             @Validated(UserRequestDto.updateGroup.class)
                                             @RequestBody UserRequestDto dto) throws AccessDeniedException {

        userService.updateUser(dto, authentication.getName());
        return ResponseEntity.ok("update完了");
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponseDto> getUserInfo(Authentication authentication) {
        UserResponseDto result = userService.getUserInfo(authentication.getName());
        return ResponseEntity.ok(result);

    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(Authentication authentication,
                                             @Validated(UserRequestDto.deleteGroup.class)
                                             @RequestBody UserRequestDto dto) throws AccessDeniedException {

        userService.deleteUser(dto, authentication.getName());
        return ResponseEntity.ok("ユーザー削除完了");

    }

}
