package devfox.board.controller;

import devfox.board.dto.request.RefreshRequestDto;
import devfox.board.dto.response.JWTResponseDTO;
import devfox.board.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    // 소셜 로그인 후
    // 쿠키에 있는 RefreshToken → Access/Refresh 재발급
    @PostMapping(
            value = "/jwt/exchange",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public JWTResponseDTO jwtExchangeApi(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return jwtService.cookieToHeader(request, response);
    }

    // ✅ Access 만료 시
    // RefreshToken으로 Access 재발급 (rotate)
    @PostMapping(
            value = "/jwt/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public JWTResponseDTO jwtRefreshApi(
            @Validated @RequestBody RefreshRequestDto dto
    ) {
        return jwtService.refreshRotate(dto);
    }
}
